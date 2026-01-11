package com.example.shiyanshi.service;

import com.example.shiyanshi.entity.TimeSlot;
import com.example.shiyanshi.mapper.TimeSlotMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 时间段业务逻辑层
 */
@Service
public class TimeSlotService {

    @Autowired
    private TimeSlotMapper timeSlotMapper;

    /**
     * 根据ID查询时间段
     */
    public TimeSlot findById(Long id) {
        return timeSlotMapper.findById(id);
    }

    /**
     * 查询所有时间段
     */
    public List<TimeSlot> findAll() {
        return timeSlotMapper.findAll();
    }

    /**
     * 查询启用的时间段
     */
    public List<TimeSlot> findEnabled() {
        return timeSlotMapper.findEnabled();
    }

    /**
     * 根据状态查询时间段
     */
    public List<TimeSlot> findByStatus(Integer status) {
        return timeSlotMapper.findByStatus(status);
    }

    /**
     * 添加时间段
     */
    @Transactional
    public int add(TimeSlot timeSlot) {
        // 默认设置为启用状态
        if (timeSlot.getStatus() == null) {
            timeSlot.setStatus(1);
        }
        // 如果没有设置排序，获取当前最大排序值+1
        if (timeSlot.getSortOrder() == null) {
            List<TimeSlot> allSlots = timeSlotMapper.findAll();
            int maxOrder = allSlots.stream()
                    .mapToInt(TimeSlot::getSortOrder)
                    .max()
                    .orElse(0);
            timeSlot.setSortOrder(maxOrder + 1);
        }
        return timeSlotMapper.insert(timeSlot);
    }

    /**
     * 更新时间段
     */
    @Transactional
    public int update(TimeSlot timeSlot) {
        return timeSlotMapper.update(timeSlot);
    }

    /**
     * 更新时间段状态
     */
    @Transactional
    public int updateStatus(Long id, Integer status) {
        return timeSlotMapper.updateStatus(id, status);
    }

    /**
     * 删除时间段
     */
    @Transactional
    public int delete(Long id) {
        return timeSlotMapper.deleteById(id);
    }

    /**
     * 批量更新排序
     */
    @Transactional
    public int batchUpdateSort(List<Map<String, Object>> sortList) {
        int count = 0;
        for (Map<String, Object> item : sortList) {
            Long id = Long.valueOf(item.get("id").toString());
            Integer sortOrder = Integer.valueOf(item.get("sortOrder").toString());
            
            TimeSlot timeSlot = timeSlotMapper.findById(id);
            if (timeSlot != null) {
                timeSlot.setSortOrder(sortOrder);
                count += timeSlotMapper.update(timeSlot);
            }
        }
        return count;
    }

    /**
     * 获取统计信息
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        List<TimeSlot> allSlots = timeSlotMapper.findAll();
        List<TimeSlot> enabledSlots = timeSlotMapper.findEnabled();
        
        stats.put("totalCount", allSlots.size());
        stats.put("enabledCount", enabledSlots.size());
        stats.put("disabledCount", allSlots.size() - enabledSlots.size());
        
        return stats;
    }

    /**
     * 检查时间段是否冲突
     */
    public boolean checkTimeConflict(String startTime, String endTime, Long excludeId) {
        List<TimeSlot> allSlots = timeSlotMapper.findAll();
        
        for (TimeSlot slot : allSlots) {
            // 排除当前编辑的时间段
            if (excludeId != null && slot.getId().equals(excludeId)) {
                continue;
            }
            
            // 检查时间是否重叠
            if (isTimeOverlap(startTime, endTime, slot.getStartTime(), slot.getEndTime())) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * 判断两个时间段是否重叠
     */
    private boolean isTimeOverlap(String start1, String end1, String start2, String end2) {
        // start1 < end2 && end1 > start2
        return start1.compareTo(end2) < 0 && end1.compareTo(start2) > 0;
    }
}
