package com.example.shiyanshi.service;

import com.example.shiyanshi.entity.Laboratory;
import com.example.shiyanshi.mapper.LaboratoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 实验室服务层
 */
@Service
public class LaboratoryService {
    
    @Autowired
    private LaboratoryMapper laboratoryMapper;
    
    /**
     * 根据ID查询实验室
     */
    public Laboratory findById(Long id) {
        return laboratoryMapper.findById(id);
    }
    
    /**
     * 查询所有实验室
     */
    public List<Laboratory> findAll() {
        return laboratoryMapper.findAll();
    }
    
    /**
     * 根据状态查询实验室
     */
    public List<Laboratory> findByStatus(Integer status) {
        return laboratoryMapper.findByStatus(status);
    }
    
    /**
     * 根据类型查询实验室
     */
    public List<Laboratory> findByType(String labType) {
        return laboratoryMapper.findByType(labType);
    }
    
    /**
     * 添加实验室
     */
    public Laboratory add(Laboratory laboratory) {
        // 检查实验室编号是否已存在
        Laboratory existLab = laboratoryMapper.findByLabNumber(laboratory.getLabNumber());
        if (existLab != null) {
            throw new RuntimeException("实验室编号已存在");
        }
        // 设置默认状态
        if (laboratory.getStatus() == null) {
            laboratory.setStatus(1);
        }
        laboratoryMapper.insert(laboratory);
        return laboratory;
    }
    
    /**
     * 更新实验室信息
     */
    public void update(Laboratory laboratory) {
        Laboratory existLab = laboratoryMapper.findById(laboratory.getId());
        if (existLab == null) {
            throw new RuntimeException("实验室不存在");
        }
        // 如果修改了实验室编号，检查新编号是否已被使用
        if (!existLab.getLabNumber().equals(laboratory.getLabNumber())) {
            Laboratory labByNumber = laboratoryMapper.findByLabNumber(laboratory.getLabNumber());
            if (labByNumber != null) {
                throw new RuntimeException("实验室编号已存在");
            }
        }
        laboratoryMapper.updateById(laboratory);
    }
    
    /**
     * 删除实验室
     */
    public void deleteById(Long id) {
        Laboratory laboratory = laboratoryMapper.findById(id);
        if (laboratory == null) {
            throw new RuntimeException("实验室不存在");
        }
        laboratoryMapper.deleteById(id);
    }
    
    /**
     * 搜索实验室
     */
    public List<Laboratory> search(String keyword, String labType, Integer status) {
        return laboratoryMapper.search(keyword, labType, status);
    }
    
    /**
     * 更新实验室状态
     */
    public void updateStatus(Long id, Integer status) {
        Laboratory laboratory = laboratoryMapper.findById(id);
        if (laboratory == null) {
            throw new RuntimeException("实验室不存在");
        }
        laboratory.setStatus(status);
        laboratoryMapper.updateById(laboratory);
    }
}
