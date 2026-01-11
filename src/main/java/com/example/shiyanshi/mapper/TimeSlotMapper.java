package com.example.shiyanshi.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.shiyanshi.entity.TimeSlot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 时间段数据访问层
 * 继承BaseMapper获取基础CRUD方法
 */
@Mapper
public interface TimeSlotMapper extends BaseMapper<TimeSlot> {
    
    /**
     * 根据ID查询时间段
     */
    default TimeSlot findById(Long id) {
        return selectById(id);
    }
    
    /**
     * 查询所有时间段
     */
    default List<TimeSlot> findAll() {
        return selectList(new LambdaQueryWrapper<TimeSlot>()
                .orderByAsc(TimeSlot::getSortOrder));
    }
    
    /**
     * 查询启用的时间段
     */
    default List<TimeSlot> findEnabled() {
        return selectList(new LambdaQueryWrapper<TimeSlot>()
                .eq(TimeSlot::getStatus, 1)
                .orderByAsc(TimeSlot::getSortOrder));
    }
    
    /**
     * 根据状态查询时间段
     */
    default List<TimeSlot> findByStatus(Integer status) {
        return selectList(new LambdaQueryWrapper<TimeSlot>()
                .eq(TimeSlot::getStatus, status)
                .orderByAsc(TimeSlot::getSortOrder));
    }
    
    /**
     * 更新时间段
     */
    default int update(TimeSlot timeSlot) {
        return updateById(timeSlot);
    }
    
    /**
     * 更新时间段状态
     */
    default int updateStatus(@Param("id") Long id, @Param("status") Integer status) {
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setId(id);
        timeSlot.setStatus(status);
        return updateById(timeSlot);
    }
}
