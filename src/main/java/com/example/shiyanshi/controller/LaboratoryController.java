package com.example.shiyanshi.controller;

import com.example.shiyanshi.annotation.RequirePermission;
import com.example.shiyanshi.common.Result;
import com.example.shiyanshi.entity.Laboratory;
import com.example.shiyanshi.service.LaboratoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 实验室管理控制器
 * 提供实验室的增删改查和管理功能
 */
@RestController
@RequestMapping("/api/laboratory")
@CrossOrigin
public class LaboratoryController {

    @Autowired
    private LaboratoryService laboratoryService;

    /**
     * 添加实验室（需要管理员及以上权限）
     * POST /api/laboratory
     */
    @RequirePermission(value = 2, description = "添加实验室需要管理员及以上权限")
    @PostMapping
    public Result add(@RequestBody Laboratory laboratory) {
        try {
            Laboratory result = laboratoryService.add(laboratory);
            if (result != null) {
                return Result.success("实验室添加成功", result);
            }
            return Result.error("实验室添加失败");
        } catch (Exception e) {
            return Result.error("添加实验室时发生错误：" + e.getMessage());
        }
    }

    /**
     * 根据ID查询实验室
     * GET /api/laboratory/{id}
     */
    @GetMapping("/{id}")
    public Result findById(@PathVariable Long id) {
        try {
            Laboratory laboratory = laboratoryService.findById(id);
            if (laboratory != null) {
                return Result.success(laboratory);
            }
            return Result.error("未找到该实验室");
        } catch (Exception e) {
            return Result.error("查询实验室时发生错误：" + e.getMessage());
        }
    }

    /**
     * 查询所有实验室
     * GET /api/laboratory/list
     */
    @GetMapping("/list")
    public Result findAll() {
        try {
            List<Laboratory> list = laboratoryService.findAll();
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("查询实验室列表时发生错误：" + e.getMessage());
        }
    }

    /**
     * 根据类型查询实验室
     * GET /api/laboratory/type/{labType}
     */
    @GetMapping("/type/{labType}")
    public Result findByType(@PathVariable String labType) {
        try {
            List<Laboratory> list = laboratoryService.findByType(labType);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("查询实验室时发生错误：" + e.getMessage());
        }
    }

    /**
     * 根据状态查询实验室
     * GET /api/laboratory/status/{status}
     */
    @GetMapping("/status/{status}")
    public Result findByStatus(@PathVariable Integer status) {
        try {
            List<Laboratory> list = laboratoryService.findByStatus(status);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("查询实验室时发生错误：" + e.getMessage());
        }
    }

    /**
     * 查询可用实验室（状态为1-可用）
     * GET /api/laboratory/available
     */
    @GetMapping("/available")
    public Result findAvailable() {
        try {
            List<Laboratory> list = laboratoryService.findByStatus(1);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("查询可用实验室时发生错误：" + e.getMessage());
        }
    }

    /**
     * 更新实验室信息
     * PUT /api/laboratory
     */
    @RequirePermission(value = 2, description = "更新实验室信息需要管理员及以上权限")
    @PutMapping
    public Result update(@RequestBody Laboratory laboratory) {
        try {
            if (laboratory.getId() == null) {
                return Result.error("实验室ID不能为空");
            }
            laboratoryService.update(laboratory);
            return Result.success("实验室信息更新成功");
        } catch (Exception e) {
            return Result.error("更新实验室信息时发生错误：" + e.getMessage());
        }
    }

    /**
     * 更新实验室状态
     * PUT /api/laboratory/status
     */
    @RequirePermission(value = 2, description = "更新实验室状态需要管理员及以上权限")
    @PutMapping("/status")
    public Result updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        try {
            if (id == null || status == null) {
                return Result.error("实验室ID和状态不能为空");
            }
            if (status < 0 || status > 2) {
                return Result.error("状态值无效，应为0-维护中/1-可用/2-停用");
            }
            laboratoryService.updateStatus(id, status);
            return Result.success("实验室状态更新成功");
        } catch (Exception e) {
            return Result.error("更新实验室状态时发生错误：" + e.getMessage());
        }
    }

    /**
     * 删除实验室
     * DELETE /api/laboratory/{id}
     */
    @RequirePermission(value = 3, description = "删除实验室需要超级管理员权限")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        try {
            laboratoryService.deleteById(id);
            return Result.success("实验室删除成功");
        } catch (Exception e) {
            return Result.error("删除实验室时发生错误：" + e.getMessage());
        }
    }

    /**
     * 搜索实验室
     * GET /api/laboratory/search
     * 支持按实验室名称、编号、位置搜索
     */
    @GetMapping("/search")
    public Result search(@RequestParam(required = false) String keyword,
                        @RequestParam(required = false) String labType,
                        @RequestParam(required = false) Integer status) {
        try {
            List<Laboratory> list = laboratoryService.search(keyword, labType, status);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("搜索实验室时发生错误：" + e.getMessage());
        }
    }

    /**
     * 根据容量范围查询实验室
     * GET /api/laboratory/capacity
     */
    @GetMapping("/capacity")
    public Result findByCapacity(@RequestParam(required = false) Integer minCapacity,
                                 @RequestParam(required = false) Integer maxCapacity) {
        try {
            List<Laboratory> list = laboratoryService.findAll();
            
            // 根据容量过滤
            if (minCapacity != null) {
                list.removeIf(lab -> lab.getCapacity() < minCapacity);
            }
            if (maxCapacity != null) {
                list.removeIf(lab -> lab.getCapacity() > maxCapacity);
            }
            
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("查询实验室时发生错误：" + e.getMessage());
        }
    }

    /**
     * 获取实验室统计信息
     * GET /api/laboratory/statistics
     */
    @GetMapping("/statistics")
    public Result getStatistics() {
        try {
            List<Laboratory> allLabs = laboratoryService.findAll();
            
            // 统计各种状态的实验室数量
            long totalCount = allLabs.size();
            long availableCount = allLabs.stream().filter(lab -> lab.getStatus() == 1).count();
            long maintenanceCount = allLabs.stream().filter(lab -> lab.getStatus() == 0).count();
            long disabledCount = allLabs.stream().filter(lab -> lab.getStatus() == 2).count();
            
            // 计算总容量
            int totalCapacity = allLabs.stream().mapToInt(Laboratory::getCapacity).sum();
            
            // 构造统计结果
            java.util.Map<String, Object> statistics = new java.util.HashMap<>();
            statistics.put("totalCount", totalCount);
            statistics.put("availableCount", availableCount);
            statistics.put("maintenanceCount", maintenanceCount);
            statistics.put("disabledCount", disabledCount);
            statistics.put("totalCapacity", totalCapacity);
            
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error("获取统计信息时发生错误：" + e.getMessage());
        }
    }
}
