package com.example.shiyanshi.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.shiyanshi.entity.Laboratory;
import com.example.shiyanshi.entity.Reservation;
import com.example.shiyanshi.entity.User;
import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 预约记录数据访问层
 * 继承MPJBaseMapper支持关联查询
 */
@Mapper
public interface ReservationMapper extends MPJBaseMapper<Reservation> {
    
    /**
     * 根据ID查询预约（带关联信息）
     */
    default Reservation findById(Long id) {
        return selectJoinOne(Reservation.class,
                new MPJLambdaWrapper<Reservation>()
                        .selectAll(Reservation.class)
                        .selectAs(User::getRealName, Reservation::getUserName)
                        .selectAs(Laboratory::getLabName, Reservation::getLabName)
                        .leftJoin(User.class, User::getId, Reservation::getUserId)
                        .leftJoin(Laboratory.class, Laboratory::getId, Reservation::getLabId)
                        .eq(Reservation::getId, id));
    }
    
    /**
     * 查询所有预约（带关联信息）
     */
    default List<Reservation> findAll() {
        return selectJoinList(Reservation.class,
                new MPJLambdaWrapper<Reservation>()
                        .selectAll(Reservation.class)
                        .selectAs(User::getRealName, Reservation::getUserName)
                        .selectAs(Laboratory::getLabName, Reservation::getLabName)
                        .leftJoin(User.class, User::getId, Reservation::getUserId)
                        .leftJoin(Laboratory.class, Laboratory::getId, Reservation::getLabId)
                        .orderByDesc(Reservation::getCreateTime));
    }
    
    /**
     * 根据用户ID查询预约
     */
    default List<Reservation> findByUserId(Long userId) {
        return selectJoinList(Reservation.class,
                new MPJLambdaWrapper<Reservation>()
                        .selectAll(Reservation.class)
                        .selectAs(Laboratory::getLabName, Reservation::getLabName)
                        .leftJoin(Laboratory.class, Laboratory::getId, Reservation::getLabId)
                        .eq(Reservation::getUserId, userId)
                        .orderByDesc(Reservation::getCreateTime));
    }
    
    /**
     * 根据实验室ID查询预约
     */
    default List<Reservation> findByLabId(Long labId) {
        return selectJoinList(Reservation.class,
                new MPJLambdaWrapper<Reservation>()
                        .selectAll(Reservation.class)
                        .selectAs(User::getRealName, Reservation::getUserName)
                        .leftJoin(User.class, User::getId, Reservation::getUserId)
                        .eq(Reservation::getLabId, labId)
                        .orderByDesc(Reservation::getReserveDate)
                        .orderByAsc(Reservation::getTimeSlot));
    }
    
    /**
     * 根据状态查询预约
     */
    default List<Reservation> findByStatus(Integer status) {
        return selectJoinList(Reservation.class,
                new MPJLambdaWrapper<Reservation>()
                        .selectAll(Reservation.class)
                        .selectAs(User::getRealName, Reservation::getUserName)
                        .selectAs(Laboratory::getLabName, Reservation::getLabName)
                        .leftJoin(User.class, User::getId, Reservation::getUserId)
                        .leftJoin(Laboratory.class, Laboratory::getId, Reservation::getLabId)
                        .eq(Reservation::getStatus, status)
                        .orderByDesc(Reservation::getCreateTime));
    }
    
    /**
     * 查询待审核的预约
     */
    default List<Reservation> findPendingReservations() {
        return selectJoinList(Reservation.class,
                new MPJLambdaWrapper<Reservation>()
                        .selectAll(Reservation.class)
                        .selectAs(User::getRealName, Reservation::getUserName)
                        .selectAs(Laboratory::getLabName, Reservation::getLabName)
                        .leftJoin(User.class, User::getId, Reservation::getUserId)
                        .leftJoin(Laboratory.class, Laboratory::getId, Reservation::getLabId)
                        .eq(Reservation::getStatus, 0)
                        .orderByAsc(Reservation::getCreateTime));
    }
    
    /**
     * 检查时间冲突
     */
    default int checkTimeConflict(@Param("labId") Long labId, 
                                  @Param("reserveDate") LocalDate reserveDate, 
                                  @Param("timeSlot") String timeSlot) {
        Long count = selectCount(new LambdaQueryWrapper<Reservation>()
                .eq(Reservation::getLabId, labId)
                .eq(Reservation::getReserveDate, reserveDate)
                .eq(Reservation::getTimeSlot, timeSlot)
                .in(Reservation::getStatus, 0, 1));
        return count != null ? count.intValue() : 0;
    }
    
    /**
     * 审核预约
     */
    default int approve(@Param("id") Long id, @Param("status") Integer status, 
                       @Param("approver") String approver, @Param("approveComment") String approveComment) {
        Reservation reservation = new Reservation();
        reservation.setId(id);
        reservation.setStatus(status);
        reservation.setApprover(approver);
        reservation.setApproveComment(approveComment);
        reservation.setApproveTime(java.time.LocalDateTime.now());
        return updateById(reservation);
    }
    
    /**
     * 取消预约
     */
    default int cancel(@Param("id") Long id, @Param("cancelReason") String cancelReason) {
        Reservation reservation = new Reservation();
        reservation.setId(id);
        reservation.setStatus(3);
        reservation.setCancelReason(cancelReason);
        return updateById(reservation);
    }
    
    /**
     * 完成预约并评价
     */
    default int complete(@Param("id") Long id, @Param("rating") Integer rating, @Param("comment") String comment) {
        Reservation reservation = new Reservation();
        reservation.setId(id);
        reservation.setStatus(4);
        reservation.setRating(rating);
        reservation.setComment(comment);
        return updateById(reservation);
    }
    
    /**
     * 搜索预约
     */
    default List<Reservation> search(@Param("keyword") String keyword, 
                                     @Param("status") Integer status,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate) {
        MPJLambdaWrapper<Reservation> wrapper = new MPJLambdaWrapper<Reservation>()
                .selectAll(Reservation.class)
                .selectAs(User::getRealName, Reservation::getUserName)
                .selectAs(Laboratory::getLabName, Reservation::getLabName)
                .leftJoin(User.class, User::getId, Reservation::getUserId)
                .leftJoin(Laboratory.class, Laboratory::getId, Reservation::getLabId);
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w
                    .like(User::getRealName, keyword)
                    .or().like(Laboratory::getLabName, keyword)
                    .or().like(Reservation::getExperimentName, keyword));
        }
        
        if (status != null) {
            wrapper.eq(Reservation::getStatus, status);
        }
        
        if (startDate != null) {
            wrapper.ge(Reservation::getReserveDate, startDate);
        }
        
        if (endDate != null) {
            wrapper.le(Reservation::getReserveDate, endDate);
        }
        
        wrapper.orderByDesc(Reservation::getCreateTime);
        
        return selectJoinList(Reservation.class, wrapper);
    }
    
    /**
     * 统计用户预约次数
     */
    default int countByUserId(Long userId) {
        Long count = selectCount(new LambdaQueryWrapper<Reservation>()
                .eq(Reservation::getUserId, userId)
                .in(Reservation::getStatus, 1, 4));
        return count != null ? count.intValue() : 0;
    }
    
    /**
     * 统计实验室预约次数
     */
    default int countByLabId(Long labId) {
        Long count = selectCount(new LambdaQueryWrapper<Reservation>()
                .eq(Reservation::getLabId, labId)
                .in(Reservation::getStatus, 1, 4));
        return count != null ? count.intValue() : 0;
    }
    
    /**
     * 根据实验室ID和日期查询预约
     */
    default List<Reservation> findByLabIdAndDate(@Param("labId") Long labId, @Param("reserveDate") LocalDate reserveDate) {
        return selectJoinList(Reservation.class,
                new MPJLambdaWrapper<Reservation>()
                        .selectAll(Reservation.class)
                        .selectAs(User::getRealName, Reservation::getUserName)
                        .leftJoin(User.class, User::getId, Reservation::getUserId)
                        .eq(Reservation::getLabId, labId)
                        .eq(Reservation::getReserveDate, reserveDate)
                        .orderByAsc(Reservation::getTimeSlot));
    }
}
