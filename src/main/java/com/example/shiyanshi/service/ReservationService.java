package com.example.shiyanshi.service;

import com.example.shiyanshi.entity.Laboratory;
import com.example.shiyanshi.entity.Reservation;
import com.example.shiyanshi.entity.User;
import com.example.shiyanshi.mapper.LaboratoryMapper;
import com.example.shiyanshi.mapper.ReservationMapper;
import com.example.shiyanshi.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预约服务层
 */
@Slf4j
@Service
public class ReservationService {
    
    @Autowired
    private ReservationMapper reservationMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private LaboratoryMapper laboratoryMapper;
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * 根据ID查询预约
     */
    public Reservation findById(Long id) {
        return reservationMapper.findById(id);
    }
    
    /**
     * 查询所有预约
     */
    public List<Reservation> findAll() {
        return reservationMapper.findAll();
    }
    
    /**
     * 根据用户ID查询预约
     */
    public List<Reservation> findByUserId(Long userId) {
        return reservationMapper.findByUserId(userId);
    }
    
    /**
     * 根据实验室ID查询预约
     */
    public List<Reservation> findByLabId(Long labId) {
        return reservationMapper.findByLabId(labId);
    }
    
    /**
     * 根据状态查询预约
     */
    public List<Reservation> findByStatus(Integer status) {
        return reservationMapper.findByStatus(status);
    }
    
    /**
     * 查询待审核的预约
     */
    public List<Reservation> findPendingReservations() {
        return reservationMapper.findPendingReservations();
    }
    
    /**
     * 创建预约
     */
    @Transactional
    public Reservation createReservation(Reservation reservation) {
        // 验证用户
        User user = userMapper.findById(reservation.getUserId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证实验室
        Laboratory laboratory = laboratoryMapper.findById(reservation.getLabId());
        if (laboratory == null) {
            throw new RuntimeException("实验室不存在");
        }
        if (laboratory.getStatus() != 1) {
            throw new RuntimeException("实验室当前不可预约");
        }
        
        // 检查预约日期是否有效
        if (reservation.getReserveDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("不能预约过去的日期");
        }
        
        // 检查时间冲突
        int conflict = reservationMapper.checkTimeConflict(
            reservation.getLabId(),
            reservation.getReserveDate(),
            reservation.getTimeSlot()
        );
        if (conflict > 0) {
            throw new RuntimeException("该时间段已被预约");
        }
        
        // 检查人数是否超过容量
        if (reservation.getPeopleNum() > laboratory.getCapacity()) {
            throw new RuntimeException("预约人数超过实验室容量");
        }
        
        // 设置用户姓名和实验室名称
        reservation.setUserName(user.getRealName());
        reservation.setLabName(laboratory.getLabName());
        reservation.setStatus(0); // 待审核
        
        reservationMapper.insert(reservation);
        return reservation;
    }
    
    /**
     * 更新预约
     */
    @Transactional
    public void update(Reservation reservation) {
        Reservation existReservation = reservationMapper.findById(reservation.getId());
        if (existReservation == null) {
            throw new RuntimeException("预约不存在");
        }
        if (existReservation.getStatus() != 0) {
            throw new RuntimeException("只能修改待审核的预约");
        }
        
        // 如果修改了日期或时间段，检查冲突
        if (!existReservation.getReserveDate().equals(reservation.getReserveDate()) ||
            !existReservation.getTimeSlot().equals(reservation.getTimeSlot())) {
            int conflict = reservationMapper.checkTimeConflict(
                reservation.getLabId(),
                reservation.getReserveDate(),
                reservation.getTimeSlot()
            );
            if (conflict > 0) {
                throw new RuntimeException("该时间段已被预约");
            }
        }
        
        reservationMapper.updateById(reservation);
    }
    
    /**
     * 审核预约
     */
    @Transactional
    public void approve(Long id, Integer status, String approver, String comment) {
        Reservation reservation = reservationMapper.findById(id);
        if (reservation == null) {
            throw new RuntimeException("预约不存在");
        }
        if (reservation.getStatus() != 0) {
            throw new RuntimeException("该预约已经审核过了");
        }
        if (status != 1 && status != 2) {
            throw new RuntimeException("审核状态无效");
        }
        reservationMapper.approve(id, status, approver, comment);
        
        // 发送审核结果通知
        try {
            User user = userMapper.findById(reservation.getUserId());
            if (user != null) {
                String statusText = status == 1 ? "已通过" : "已拒绝";
                String title = "预约审核通知";
                String content = String.format("您的预约[%s - %s %s]审核结果：%s。%s",
                    reservation.getLabName(),
                    reservation.getReserveDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    reservation.getTimeSlot(),
                    statusText,
                    comment != null ? "审核意见：" + comment : "");
                
                // 发送站内消息
                messageService.sendApprovalMessage(
                    reservation.getUserId(),
                    title,
                    content,
                    reservation.getId(),
                    1
                );
                
                // 发送邮件通知
                if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                    Map<String, Object> approvalInfo = new HashMap<>();
                    approvalInfo.put("username", user.getRealName());
                    approvalInfo.put("labName", reservation.getLabName());
                    approvalInfo.put("reservationDate", reservation.getReserveDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    approvalInfo.put("timeSlot", reservation.getTimeSlot());
                    approvalInfo.put("status", statusText);
                    approvalInfo.put("note", comment != null ? comment : "无");
                    
                    emailService.sendApprovalNotification(
                        user.getEmail(),
                        user.getRealName(),
                        approvalInfo
                    );
                }
            }
        } catch (Exception e) {
            log.error("发送审核通知失败", e);
        }
    }
    
    /**
     * 取消预约
     */
    @Transactional
    public void cancel(Long id, String cancelReason) {
        Reservation reservation = reservationMapper.findById(id);
        if (reservation == null) {
            throw new RuntimeException("预约不存在");
        }
        if (reservation.getStatus() == 3 || reservation.getStatus() == 4) {
            throw new RuntimeException("该预约不能取消");
        }
        reservationMapper.cancel(id, cancelReason);
        
        // 发送取消通知
        try {
            User user = userMapper.findById(reservation.getUserId());
            if (user != null) {
                String title = "预约取消通知";
                String content = String.format("您的预约[%s - %s %s]已被取消。%s",
                    reservation.getLabName(),
                    reservation.getReserveDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    reservation.getTimeSlot(),
                    cancelReason != null ? "取消原因：" + cancelReason : "");
                
                // 发送站内消息
                messageService.sendSystemMessage(
                    reservation.getUserId(),
                    title,
                    content,
                    1
                );
                
                // 发送邮件通知
                if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                    Map<String, Object> cancelInfo = new HashMap<>();
                    cancelInfo.put("username", user.getRealName());
                    cancelInfo.put("labName", reservation.getLabName());
                    cancelInfo.put("reservationDate", reservation.getReserveDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    cancelInfo.put("timeSlot", reservation.getTimeSlot());
                    cancelInfo.put("status", "已取消");
                    cancelInfo.put("note", cancelReason != null ? cancelReason : "无");
                    
                    emailService.sendApprovalNotification(
                        user.getEmail(),
                        user.getRealName(),
                        cancelInfo
                    );
                }
            }
        } catch (Exception e) {
            log.error("发送取消通知失败", e);
        }
    }
    
    /**
     * 完成预约并评价
     */
    @Transactional
    public void complete(Long id, Integer rating, String comment) {
        Reservation reservation = reservationMapper.findById(id);
        if (reservation == null) {
            throw new RuntimeException("预约不存在");
        }
        if (reservation.getStatus() != 1) {
            throw new RuntimeException("只能完成已通过审核的预约");
        }
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("评分必须在1-5之间");
        }
        reservationMapper.complete(id, rating, comment);
        
        // 发送完成通知
        try {
            User user = userMapper.findById(reservation.getUserId());
            if (user != null) {
                String title = "预约完成通知";
                String content = String.format("您的预约[%s - %s %s]已完成。评分：%d星。%s",
                    reservation.getLabName(),
                    reservation.getReserveDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    reservation.getTimeSlot(),
                    rating,
                    comment != null ? "评价：" + comment : "");
                
                // 发送站内消息
                messageService.sendSystemMessage(
                    reservation.getUserId(),
                    title,
                    content,
                    0
                );
            }
        } catch (Exception e) {
            log.error("发送完成通知失败", e);
        }
    }
    
    /**
     * 删除预约
     */
    public void deleteById(Long id) {
        Reservation reservation = reservationMapper.findById(id);
        if (reservation == null) {
            throw new RuntimeException("预约不存在");
        }
        reservationMapper.deleteById(id);
    }
    
    /**
     * 搜索预约
     */
    public List<Reservation> search(String keyword, Integer status, LocalDate startDate, LocalDate endDate) {
        return reservationMapper.search(keyword, status, startDate, endDate);
    }
    
    /**
     * 统计用户预约次数
     */
    public int countByUserId(Long userId) {
        return reservationMapper.countByUserId(userId);
    }
    
    /**
     * 统计实验室预约次数
     */
    public int countByLabId(Long labId) {
        return reservationMapper.countByLabId(labId);
    }
    
    /**
     * 审核预约（通过）
     */
    @Transactional
    public void approveReservation(Long id, String approvalNote) {
        approve(id, 1, null, approvalNote);
    }
    
    /**
     * 审核预约（拒绝）
     */
    @Transactional
    public void rejectReservation(Long id, String approvalNote) {
        approve(id, 2, null, approvalNote);
    }
    
    /**
     * 取消预约
     */
    @Transactional
    public void cancelReservation(Long id) {
        cancel(id, null);
    }
    
    /**
     * 完成预约
     */
    @Transactional
    public void completeReservation(Long id, String feedback) {
        complete(id, 5, feedback);
    }
    
    /**
     * 检查时间冲突
     */
    public boolean checkTimeConflict(Long labId, String reserveDate, String timeSlot) {
        LocalDate date = LocalDate.parse(reserveDate);
        int conflict = reservationMapper.checkTimeConflict(labId, date, timeSlot);
        return conflict > 0;
    }
    
    /**
     * 根据日期范围查询预约
     */
    public List<Reservation> findByDateRange(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return search(null, null, start, end);
    }
    
    /**
     * 根据实验室ID和日期查询预约
     */
    public List<Reservation> findByLabIdAndDate(Long labId, String date) {
        LocalDate reserveDate = LocalDate.parse(date);
        return reservationMapper.findByLabIdAndDate(labId, reserveDate);
    }

    /**
     * 根据用户与状态统计预约数量
     */
    public int countByUserIdAndStatus(Long userId, Integer status) {
        Long count = reservationMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Reservation>()
                        .eq(Reservation::getUserId, userId)
                        .eq(Reservation::getStatus, status)
        );
        return count != null ? count.intValue() : 0;
    }

    /**
     * 获取指定用户的预约统计数据
     * - total: 总预约次数（状态 in [1,4] 与现有 countByUserId 同口径保持）
     * - pending: 待审核(0)
     * - approved: 已通过(1)
     * - rejected: 已拒绝(2)
     * - canceled: 已取消(3)
     * - completed: 已完成(4)
     */
    public Map<String, Integer> getUserReservationStats(Long userId) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total", countByUserId(userId));
        stats.put("pending", countByUserIdAndStatus(userId, 0));
        stats.put("approved", countByUserIdAndStatus(userId, 1));
        stats.put("rejected", countByUserIdAndStatus(userId, 2));
        stats.put("canceled", countByUserIdAndStatus(userId, 3));
        stats.put("completed", countByUserIdAndStatus(userId, 4));
        return stats;
    }
}
