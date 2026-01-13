package com.example.shiyanshi.controller;

import com.example.shiyanshi.annotation.RequirePermission;
import com.example.shiyanshi.common.Result;
import com.example.shiyanshi.entity.Reservation;
import com.example.shiyanshi.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预约管理控制器
 * 提供预约的创建、审核、查询、取消等功能
 */
@RestController
@RequestMapping("/api/reservation")
@CrossOrigin
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    /**
     * 创建预约
     * POST /api/reservation
     */
    @PostMapping
    public Result create(@RequestBody Reservation reservation) {
        try {
            if (reservation.getUserId() == null || reservation.getLabId() == null || 
                reservation.getReserveDate() == null || reservation.getTimeSlot() == null) {
                return Result.error("预约信息不完整");
            }
            Reservation result = reservationService.createReservation(reservation);
            return Result.success("预约创建成功", result);
        } catch (Exception e) {
            return Result.error("创建预约时发生错误：" + e.getMessage());
        }
    }

    /**
     * 根据ID查询预约
     * GET /api/reservation/{id}
     */
    @GetMapping("/{id}")
    public Result findById(@PathVariable Long id) {
        try {
            Reservation reservation = reservationService.findById(id);
            if (reservation != null) {
                return Result.success(reservation);
            }
            return Result.error("未找到该预约记录");
        } catch (Exception e) {
            return Result.error("查询预约时发生错误：" + e.getMessage());
        }
    }

    /**
     * 查询所有预约
     * GET /api/reservation/list
     */
    @GetMapping("/list")
    public Result findAll() {
        try {
            List<Reservation> list = reservationService.findAll();
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("查询预约列表时发生错误：" + e.getMessage());
        }
    }

    /**
     * 根据用户ID查询预约
     * GET /api/reservation/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public Result findByUserId(@PathVariable Long userId) {
        try {
            List<Reservation> list = reservationService.findByUserId(userId);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("查询用户预约时发生错误：" + e.getMessage());
        }
    }

    /**
     * 根据实验室ID查询预约
     * GET /api/reservation/lab/{labId}
     */
    @GetMapping("/lab/{labId}")
    public Result findByLabId(@PathVariable Long labId) {
        try {
            List<Reservation> list = reservationService.findByLabId(labId);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("查询实验室预约时发生错误：" + e.getMessage());
        }
    }

    /**
     * 根据状态查询预约
     * GET /api/reservation/status/{status}
     * 状态：0-待审核, 1-已通过, 2-已拒绝, 3-已取消, 4-已完成
     */
    @GetMapping("/status/{status}")
    public Result findByStatus(@PathVariable Integer status) {
        try {
            List<Reservation> list = reservationService.findByStatus(status);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("查询预约时发生错误：" + e.getMessage());
        }
    }

    /**
     * 查询待审核预约
     * GET /api/reservation/pending
     */
    @GetMapping("/pending")
    public Result findPending() {
        try {
            List<Reservation> list = reservationService.findByStatus(0);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("查询待审核预约时发生错误：" + e.getMessage());
        }
    }

    /**
     * 审核预约（通过）（需要管理员及以上权限）
     * PUT /api/reservation/approve/{id}
     */
    @RequirePermission(value = 2, description = "审核预约需要管理员及以上权限")
    @PutMapping("/approve/{id}")
    public Result approve(@PathVariable Long id, @RequestParam(required = false) String approvalNote) {
        try {
            reservationService.approveReservation(id, approvalNote);
            return Result.success("预约审核通过");
        } catch (Exception e) {
            return Result.error("审核预约时发生错误：" + e.getMessage());
        }
    }

    /**
     * 审核预约（拒绝）（需要管理员及以上权限）
     * PUT /api/reservation/reject/{id}
     */
    @RequirePermission(value = 2, description = "审核预约需要管理员及以上权限")
    @PutMapping("/reject/{id}")
    public Result reject(@PathVariable Long id, @RequestParam(required = false) String approvalNote) {
        try {
            reservationService.rejectReservation(id, approvalNote);
            return Result.success("预约已拒绝");
        } catch (Exception e) {
            return Result.error("拒绝预约时发生错误：" + e.getMessage());
        }
    }

    /**
     * 取消预约
     * PUT /api/reservation/cancel/{id}
     */
    @PutMapping("/cancel/{id}")
    public Result cancel(@PathVariable Long id) {
        try {
            reservationService.cancelReservation(id);
            return Result.success("预约已取消");
        } catch (Exception e) {
            return Result.error("取消预约时发生错误：" + e.getMessage());
        }
    }

    /**
     * 完成预约
     * PUT /api/reservation/complete/{id}
     */
    @PutMapping("/complete/{id}")
    public Result complete(@PathVariable Long id, @RequestParam(required = false) String feedback) {
        try {
            reservationService.completeReservation(id, feedback);
            return Result.success("预约已完成");
        } catch (Exception e) {
            return Result.error("完成预约时发生错误：" + e.getMessage());
        }
    }

    /**
     * 更新预约信息
     * PUT /api/reservation
     */
    @RequirePermission(value = 2, description = "更新预约信息需要管理员及以上权限")
    @PutMapping
    public Result update(@RequestBody Reservation reservation) {
        try {
            if (reservation.getId() == null) {
                return Result.error("预约ID不能为空");
            }
            reservationService.update(reservation);
            return Result.success("预约信息更新成功");
        } catch (Exception e) {
            return Result.error("更新预约信息时发生错误：" + e.getMessage());
        }
    }

    /**
     * 删除预约
     * DELETE /api/reservation/{id}
     */
    @RequirePermission(value = 3, description = "删除预约需要超级管理员权限")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        try {
            reservationService.deleteById(id);
            return Result.success("预约删除成功");
        } catch (Exception e) {
            return Result.error("删除预约时发生错误：" + e.getMessage());
        }
    }

    /**
     * 检查时间冲突
     * GET /api/reservation/check-conflict
     */
    @GetMapping("/check-conflict")
    public Result checkConflict(@RequestParam Long labId, 
                                @RequestParam String reserveDate, 
                                @RequestParam String timeSlot) {
        try {
            boolean hasConflict = reservationService.checkTimeConflict(labId, reserveDate, timeSlot);
            if (hasConflict) {
                return Result.error("该时间段已被预约");
            }
            return Result.success("该时间段可用");
        } catch (Exception e) {
            return Result.error("检查时间冲突时发生错误：" + e.getMessage());
        }
    }

    /**
     * 获取预约统计信息
     * GET /api/reservation/statistics
     */
    @GetMapping("/statistics")
    public Result getStatistics(@RequestParam(required = false) Long userId) {
        try {
            List<Reservation> reservations;
            if (userId != null) {
                reservations = reservationService.findByUserId(userId);
            } else {
                reservations = reservationService.findAll();
            }
            
            // 统计各种状态的预约数量
            long totalCount = reservations.size();
            long pendingCount = reservations.stream().filter(r -> r.getStatus() == 0).count();
            long approvedCount = reservations.stream().filter(r -> r.getStatus() == 1).count();
            long rejectedCount = reservations.stream().filter(r -> r.getStatus() == 2).count();
            long cancelledCount = reservations.stream().filter(r -> r.getStatus() == 3).count();
            long completedCount = reservations.stream().filter(r -> r.getStatus() == 4).count();
            
            // 构造统计结果
            java.util.Map<String, Object> statistics = new java.util.HashMap<>();
            statistics.put("totalCount", totalCount);
            statistics.put("pendingCount", pendingCount);
            statistics.put("approvedCount", approvedCount);
            statistics.put("rejectedCount", rejectedCount);
            statistics.put("cancelledCount", cancelledCount);
            statistics.put("completedCount", completedCount);
            
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error("获取统计信息时发生错误：" + e.getMessage());
        }
    }

    /**
     * 根据日期范围查询预约
     * GET /api/reservation/date-range
     */
    @GetMapping("/date-range")
    public Result findByDateRange(@RequestParam String startDate, @RequestParam String endDate) {
        try {
            List<Reservation> list = reservationService.findByDateRange(startDate, endDate);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("查询预约时发生错误：" + e.getMessage());
        }
    }

    /**
     * 获取实验室在指定日期的预约情况
     * GET /api/reservation/lab-schedule
     */
    @GetMapping("/lab-schedule")
    public Result getLabSchedule(@RequestParam Long labId, @RequestParam String date) {
        try {
            List<Reservation> list = reservationService.findByLabIdAndDate(labId, date);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("查询实验室预约情况时发生错误：" + e.getMessage());
        }
    }
}
