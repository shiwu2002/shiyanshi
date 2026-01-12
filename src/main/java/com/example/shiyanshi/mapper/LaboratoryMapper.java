package com.example.shiyanshi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.shiyanshi.entity.Laboratory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 实验室数据访问层
 * 继承BaseMapper后，自动拥有基础的CRUD方法
 */
@Mapper
public interface LaboratoryMapper extends BaseMapper<Laboratory> {
    
    /**
     * 根据ID查询实验室
     */
    default Laboratory findById(Long id) {
        return selectById(id);
    }
    
    /**
     * 查询所有实验室
     */
    default java.util.List<Laboratory> findAll() {
        return selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Laboratory>()
                .orderByDesc(Laboratory::getCreateTime));
    }
    
    /**
     * 根据状态查询实验室
     */
    default java.util.List<Laboratory> findByStatus(Integer status) {
        return selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Laboratory>()
                .eq(Laboratory::getStatus, status)
                .orderByDesc(Laboratory::getCreateTime));
    }
    
    /**
     * 根据类型查询实验室
     */
    default java.util.List<Laboratory> findByType(String labType) {
        // 数据库无实验室类型字段，暂不按类型过滤
        return selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Laboratory>()
                .eq(Laboratory::getStatus, 1)
                .orderByDesc(Laboratory::getCreateTime));
    }
    
    /**
     * 搜索实验室
     */
    default java.util.List<Laboratory> search(@Param("keyword") String keyword, 
                                              @Param("labType") String labType, 
                                              @Param("status") Integer status) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Laboratory> wrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w
                    .like(Laboratory::getLabName, keyword)
                    .or().like(Laboratory::getLabNumber, keyword)
                    .or().like(Laboratory::getLocation, keyword));
        }
        
        // 数据库无实验室类型字段，忽略labType过滤
        
        if (status != null) {
            wrapper.eq(Laboratory::getStatus, status);
        }
        
        wrapper.orderByDesc(Laboratory::getCreateTime);
        
        return selectList(wrapper);
    }
    
    /**
     * 根据实验室编号查询
     */
    default Laboratory findByLabNumber(String labNumber) {
        return selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Laboratory>()
                .eq(Laboratory::getLabNumber, labNumber));
    }
}
