package com.example.shiyanshi.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.shiyanshi.entity.Reservation;
import com.example.shiyanshi.entity.User;
import com.example.shiyanshi.mapper.ReservationMapper;
import com.example.shiyanshi.service.EmailService;
import com.example.shiyanshi.service.MessageService;
import com.example.shiyanshi.service.ReservationService;
import com.example.shiyanshi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预约提醒定时任务
 * 负责发送预约前的提醒通知（24小时、1小时、30分钟）
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class ReservationReminderTask {

    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;
    private final MessageService messageService;
    private final EmailService emailService;
    private final UserService userService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * 每小时执行一次，检查并发送24小时提醒
     * 在每个小时的整点执行（00分）
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void remind24Hours() {
        log.info("开始执行24小时预约提醒任务");
        try {
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            
            // 查询明天的预约（已审核通过的）
            List<Reservation> reservations = findReservationsByDateAndStatus(tomorrow, 1);
            
            int successCount = 0;
            for (Reservation reservation : reservations) {
                try {
                    sendReminder(reservation, 24);
                    successCount++;
                } catch (Exception e) {
                    log.error("发送24小时提醒失败: reservationId={}", reservation.getId(), e);
                }
            }
            
            log.info("24小时预约提醒任务完成，共处理{}条预约，成功发送{}条提醒", 
                    reservations.size(), successCount);
        } catch (Exception e) {
            log.error("24小时预约提醒任务执行失败", e);
        }
    }

    /**
     * 每10分钟执行一次，检查并发送1小时提醒
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void remind1Hour() {
        log.info("开始执行1小时预约提醒任务");
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime oneHourLater = now.plusHours(1);
            
            // 查询1小时后的预约
            List<Reservation> reservations = findReservationsNeedReminder(oneHourLater, 60);
            
            int successCount = 0;
            for (Reservation reservation : reservations) {
                try {
                    sendReminder(reservation, 1);
                    successCount++;
                } catch (Exception e) {
                    log.error("发送1小时提醒失败: reservationId={}", reservation.getId(), e);
                }
            }
            
            log.info("1小时预约提醒任务完成，共处理{}条预约，成功发送{}条提醒", 
                    reservations.size(), successCount);
        } catch (Exception e) {
            log.error("1小时预约提醒任务执行失败", e);
        }
    }

    /**
     * 每5分钟执行一次，检查并发送30分钟提醒
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void remind30Minutes() {
        log.info("开始执行30分钟预约提醒任务");
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime thirtyMinutesLater = now.plusMinutes(30);
            
            // 查询30分钟后的预约
            List<Reservation> reservations = findReservationsNeedReminder(thirtyMinutesLater, 30);
            
            int successCount = 0;
            for (Reservation reservation : reservations) {
                try {
                    sendReminder(reservation, 0); // 0表示30分钟
                    successCount++;
                } catch (Exception e) {
                    log.error("发送30分钟提醒失败: reservationId={}", reservation.getId(), e);
                }
            }
            
            log.info("30分钟预约提醒任务完成，共处理{}条预约，成功发送{}条提醒", 
                    reservations.size(), successCount);
        } catch (Exception e) {
            log.error("30分钟预约提醒任务执行失败", e);
        }
    }

    /**
     * 每天凌晨1点执行，检查并发送当天预约的提醒
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void remindToday() {
        log.info("开始执行当天预约提醒任务");
        try {
            LocalDate today = LocalDate.now();
            
            // 查询今天的所有已审核通过的预约
            List<Reservation> reservations = findReservationsByDateAndStatus(today, 1);
            
            int successCount = 0;
            for (Reservation reservation : reservations) {
                try {
                    sendTodayReminder(reservation);
                    successCount++;
                } catch (Exception e) {
                    log.error("发送当天提醒失败: reservationId={}", reservation.getId(), e);
                }
            }
            
            log.info("当天预约提醒任务完成，共处理{}条预约，成功发送{}条提醒", 
                    reservations.size(), successCount);
        } catch (Exception e) {
            log.error("当天预约提醒任务执行失败", e);
        }
    }

    /**
     * 每天凌晨2点执行，清理过期预约的自动处理
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void handleExpiredReservations() {
        log.info("开始执行过期预约处理任务");
        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            
            // 查询昨天及之前的待审核预约，自动标记为已过期
            LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Reservation::getStatus, 0) // 待审核
                   .le(Reservation::getReserveDate, yesterday);
            
            List<Reservation> expiredReservations = reservationMapper.selectList(wrapper);
            
            for (Reservation reservation : expiredReservations) {
                try {
                    // 更新为已取消状态
                    reservation.setStatus(3); // 3-已取消
                    reservation.setCancelReason("预约超时未审核，系统自动取消");
                    reservationMapper.updateById(reservation);
                    
                    // 发送通知
                    User user = userService.findById(reservation.getUserId());
                    if (user != null) {
                        // 发送站内消息
                        messageService.sendSystemMessage(
                            user.getId(),
                            "预约已过期",
                            String.format("您的预约（%s，%s %s）因超时未审核已自动取消", 
                                reservation.getLabName(),
                                reservation.getReserveDate().format(DATE_FORMATTER),
                                reservation.getTimeSlot()),
                            0 // 普通优先级
                        );
                    }
                } catch (Exception e) {
                    log.error("处理过期预约失败: reservationId={}", reservation.getId(), e);
                }
            }
            
            log.info("过期预约处理任务完成，共处理{}条过期预约", expiredReservations.size());
        } catch (Exception e) {
            log.error("过期预约处理任务执行失败", e);
        }
    }

    /**
     * 根据日期和状态查询预约
     */
    private List<Reservation> findReservationsByDateAndStatus(LocalDate date, Integer status) {
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getReserveDate, date)
               .eq(Reservation::getStatus, status)
               .orderByAsc(Reservation::getTimeSlot);
        
        return reservationMapper.selectList(wrapper);
    }

    /**
     * 查找需要提醒的预约
     * @param targetTime 目标时间
     * @param toleranceMinutes 容差分钟数（用于匹配时间段）
     */
    private List<Reservation> findReservationsNeedReminder(LocalDateTime targetTime, int toleranceMinutes) {
        LocalDate targetDate = targetTime.toLocalDate();
        LocalTime targetLocalTime = targetTime.toLocalTime();
        
        // 查询目标日期的所有已审核通过的预约
        List<Reservation> allReservations = findReservationsByDateAndStatus(targetDate, 1);
        
        // 过滤出符合时间条件的预约
        List<Reservation> result = new ArrayList<>();
        for (Reservation reservation : allReservations) {
            try {
                // 解析时间段，格式如 "08:00-10:00"
                String timeSlot = reservation.getTimeSlot();
                if (timeSlot != null && timeSlot.contains("-")) {
                    String[] times = timeSlot.split("-");
                    if (times.length == 2) {
                        LocalTime startTime = LocalTime.parse(times[0].trim(), TIME_FORMATTER);
                        
                        // 计算时间差（分钟）
                        long minutesDiff = java.time.Duration.between(targetLocalTime, startTime).toMinutes();
                        
                        // 如果在容差范围内，则加入结果列表
                        if (Math.abs(minutesDiff) <= toleranceMinutes / 2) {
                            result.add(reservation);
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("解析预约时间失败: reservationId={}, timeSlot={}", 
                        reservation.getId(), reservation.getTimeSlot(), e);
            }
        }
        
        return result;
    }

    /**
     * 发送提醒通知
     */
    private void sendReminder(Reservation reservation, int hoursAhead) {
        User user = userService.findById(reservation.getUserId());
        if (user == null) {
            log.warn("用户不存在，无法发送提醒: userId={}", reservation.getUserId());
            return;
        }

        String timeText;
        String title;
        Integer priority;
        
        if (hoursAhead == 24) {
            timeText = "24小时";
            title = "预约提醒（明天）";
            priority = 0; // 普通优先级
        } else if (hoursAhead == 1) {
            timeText = "1小时";
            title = "预约提醒（即将开始）";
            priority = 1; // 重要优先级
        } else {
            timeText = "30分钟";
            title = "预约提醒（马上开始）";
            priority = 2; // 紧急优先级
        }

        String content = String.format(
            "您预约的实验室 %s 将于 %s 后开始使用（%s %s），请做好准备并准时到达。",
            reservation.getLabName(),
            timeText,
            reservation.getReserveDate().format(DATE_FORMATTER),
            reservation.getTimeSlot()
        );

        // 发送站内消息
        messageService.sendReminderMessage(
            user.getId(),
            title,
            content,
            reservation.getId(),
            "reservation"
        );

        // 发送邮件通知（如果用户绑定了邮箱且邮箱已验证）
        if (user.getEmail() != null && !user.getEmail().isEmpty() && user.getEmailVerified() == 1) {
            try {
                Map<String, Object> reminderInfo = new HashMap<>();
                reminderInfo.put("labName", reservation.getLabName());
                reminderInfo.put("reservationDate", reservation.getReserveDate().format(DATE_FORMATTER));
                reminderInfo.put("timeSlot", reservation.getTimeSlot());
                
                emailService.sendReservationReminder(
                    user.getEmail(),
                    user.getRealName() != null ? user.getRealName() : user.getUsername(),
                    reminderInfo
                );
            } catch (Exception e) {
                log.error("发送提醒邮件失败: userId={}, email={}", user.getId(), user.getEmail(), e);
            }
        }

        log.info("预约提醒发送成功: userId={}, reservationId={}, hoursAhead={}", 
                user.getId(), reservation.getId(), hoursAhead);
    }

    /**
     * 发送当天预约提醒
     */
    private void sendTodayReminder(Reservation reservation) {
        User user = userService.findById(reservation.getUserId());
        if (user == null) {
            log.warn("用户不存在，无法发送当天提醒: userId={}", reservation.getUserId());
            return;
        }

        String title = "今日预约提醒";
        String content = String.format(
            "您今天有一个实验室预约：%s，使用时间 %s。请合理安排时间，准时到达。",
            reservation.getLabName(),
            reservation.getTimeSlot()
        );

        // 发送站内消息
        messageService.sendReminderMessage(
            user.getId(),
            title,
            content,
            reservation.getId(),
            "reservation"
        );

        log.info("当天预约提醒发送成功: userId={}, reservationId={}", 
                user.getId(), reservation.getId());
    }
}
