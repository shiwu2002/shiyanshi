package com.example.shiyanshi.controller;

import com.example.shiyanshi.annotation.RequirePermission;
import com.example.shiyanshi.common.Result;
import com.example.shiyanshi.entity.TimeSlot;
import com.example.shiyanshi.mapper.TimeSlotMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 时间段配置控制器
 * 提供时间段的增删改查功能
 */
@RestController
@RequestMapping("/api/timeslot")
@CrossOrigin
public class TimeSlotController {

    @Autowired
    private TimeSlotMapper timeSlotMapper;

    /**
     * 添加时间段
     * POST /api/timeslot
     */
    @RequirePermission(value = 2, description = "添加时间段需要管理员及以上权限")
    @PostMapping
    public Result add(@RequestBody TimeSlot timeSlot) {
        try {
            if (timeSlot.getSlotName() == null || timeSlot.getStartTime() == null || 
                timeSlot.getEndTime() == null) {
                return Result.error("时间段信息不完整");
            }
            int result = timeSlotMapper.insert(timeSlot);
            if (result > 0) {
                return Result.success("时间段添加成功", timeSlot);
            }
            return Result.error("时间段添加失败");
        } catch (Exception e) {
            return Result.error("添加时间段时发生错误：" + e.getMessage());
        }
    }

    /**
     * 根据ID查询时间段
     * GET /api/timeslot/{id}
     */
    @GetMapping("/{id}")
    public Result findById(@PathVariable Long id) {
        try {
            TimeSlot timeSlot = timeSlotMapper.findById(id);
            if (timeSlot != null) {
                return Result.success(timeSlot);
            }
            return Result.error("未找到该时间段");
        } catch (Exception e) {
            return Result.error("查询时间段时发生错误：" + e.getMessage());
        }
    }

    /**
     * 查询所有时间段
     * GET /api/timeslot/list
     */
    @GetMapping("/list")
    public Result findAll() {
        try {
            List<TimeSlot> list = timeSlotMapper.findAll();
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("查询时间段列表时发生错误：" + e.getMessage());
        }
    }

    /**
     * 查询启用的时间段
     * GET /api/timeslot/enabled
     */
    @GetMapping("/enabled")
    public Result findEnabled() {
        try {
            List<TimeSlot> list = timeSlotMapper.findByStatus(1);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("查询启用时间段时发生错误：" + e.getMessage());
        }
    }

    /**
     * 根据状态查询时间段
     * GET /api/timeslot/status/{status}
     */
    @GetMapping("/status/{status}")
    public Result findByStatus(@PathVariable Integer status) {
        try {
            List<TimeSlot> list = timeSlotMapper.findByStatus(status);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("查询时间段时发生错误：" + e.getMessage());
        }
    }

    /**
     * 更新时间段信息
     * PUT /api/timeslot
     */
    @RequirePermission(value = 2, description = "更新时间段需要管理员及以上权限")
    @PutMapping
    public Result update(@RequestBody TimeSlot timeSlot) {
        try {
            if (timeSlot.getId() == null) {
                return Result.error("时间段ID不能为空");
            }
            TimeSlot existing = timeSlotMapper.findById(timeSlot.getId());
            if (existing == null) {
                return Result.error("时间段不存在");
            }
            int result = timeSlotMapper.update(timeSlot);
            if (result > 0) {
                return Result.success("时间段信息更新成功");
            }
            return Result.error("时间段信息更新失败");
        } catch (Exception e) {
            return Result.error("更新时间段信息时发生错误：" + e.getMessage());
        }
    }

    /**
     * 更新时间段信息（RESTful风格）
     * PUT /api/timeslot/{id}
     */
    @RequirePermission(value = 2, description = "更新时间段需要管理员及以上权限")
    @PutMapping("/{id}")
    public Result updateById(@PathVariable Long id, @RequestBody TimeSlot timeSlot) {
        try {
            if (id == null) {
                return Result.error("时间段ID不能为空");
            }
            TimeSlot existing = timeSlotMapper.findById(id);
            if (existing == null) {
                return Result.error("时间段不存在");
            }
            // 设置ID以确保更新正确的记录
            timeSlot.setId(id);
            int result = timeSlotMapper.update(timeSlot);
            if (result > 0) {
                return Result.success("时间段信息更新成功");
            }
            return Result.error("时间段信息更新失败");
        } catch (Exception e) {
            return Result.error("更新时间段信息时发生错误：" + e.getMessage());
        }
    }

    /**
     * 更新时间段状态
     * PUT /api/timeslot/status
     */
    @RequirePermission(value = 2, description = "更新时间段状态需要管理员及以上权限")
    @PutMapping("/status")
    public Result updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        try {
            if (id == null || status == null) {
                return Result.error("时间段ID和状态不能为空");
            }
            if (status < 0 || status > 1) {
                return Result.error("状态值无效，应为0-禁用/1-启用");
            }
            TimeSlot timeSlot = timeSlotMapper.findById(id);
            if (timeSlot == null) {
                return Result.error("时间段不存在");
            }
            int result = timeSlotMapper.updateStatus(id, status);
            if (result > 0) {
                return Result.success("时间段状态更新成功");
            }
            return Result.error("时间段状态更新失败");
        } catch (Exception e) {
            return Result.error("更新时间段状态时发生错误：" + e.getMessage());
        }
    }

    /**
     * 删除时间段
     * DELETE /api/timeslot/{id}
     */
    @RequirePermission(value = 3, description = "删除时间段需要超级管理员权限")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        try {
            TimeSlot timeSlot = timeSlotMapper.findById(id);
            if (timeSlot == null) {
                return Result.error("时间段不存在");
            }
            int result = timeSlotMapper.deleteById(id);
            if (result > 0) {
                return Result.success("时间段删除成功");
            }
            return Result.error("时间段删除失败");
        } catch (Exception e) {
            return Result.error("删除时间段时发生错误：" + e.getMessage());
        }
    }

    /**
     * 批量更新时间段排序
     * PUT /api/timeslot/batch-sort
     */
    @RequirePermission(value = 2, description = "批量更新时间段排序需要管理员及以上权限")
    @PutMapping("/batch-sort")
    public Result batchUpdateSort(@RequestBody List<TimeSlot> timeSlots) {
        try {
            if (timeSlots == null || timeSlots.isEmpty()) {
                return Result.error("时间段列表不能为空");
            }
            for (TimeSlot timeSlot : timeSlots) {
                if (timeSlot.getId() != null && timeSlot.getSortOrder() != null) {
                    timeSlotMapper.update(timeSlot);
                }
            }
            return Result.success("时间段排序更新成功");
        } catch (Exception e) {
            return Result.error("更新时间段排序时发生错误：" + e.getMessage());
        }
    }

    /**
     * 获取时间段统计信息
     * GET /api/timeslot/statistics
     */
    @GetMapping("/statistics")
    public Result getStatistics() {
        try {
            List<TimeSlot> allSlots = timeSlotMapper.findAll();
            
            // 统计各种状态的时间段数量
            long totalCount = allSlots.size();
            long enabledCount = allSlots.stream().filter(slot -> slot.getStatus() == 1).count();
            long disabledCount = allSlots.stream().filter(slot -> slot.getStatus() == 0).count();
            
            // 构造统计结果
            java.util.Map<String, Object> statistics = new java.util.HashMap<>();
            statistics.put("totalCount", totalCount);
            statistics.put("enabledCount", enabledCount);
            statistics.put("disabledCount", disabledCount);
            
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error("获取统计信息时发生错误：" + e.getMessage());
        }
    }
}
