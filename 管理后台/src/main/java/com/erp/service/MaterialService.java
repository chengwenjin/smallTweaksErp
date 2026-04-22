package com.erp.service;

import com.baserbac.common.enums.ResultCode;
import com.baserbac.common.exception.BusinessException;
import com.baserbac.common.result.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.dto.MaterialCreateDTO;
import com.erp.dto.MaterialQueryDTO;
import com.erp.dto.MaterialUpdateDTO;
import com.erp.entity.ErpMaterial;
import com.erp.mapper.MaterialMapper;
import com.erp.vo.MaterialVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 物料服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialMapper materialMapper;

    private String getMaterialTypeName(Integer materialType) {
        if (materialType == null) {
            return "未知";
        }
        return switch (materialType) {
            case 1 -> "原材料";
            case 2 -> "半成品";
            case 3 -> "成品";
            default -> "未知";
        };
    }

    /**
     * 分页查询物料列表
     */
    public PageResult<MaterialVO> pageMaterials(MaterialQueryDTO queryDTO) {
        Page<ErpMaterial> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<ErpMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(queryDTO.getMaterialCode() != null, ErpMaterial::getMaterialCode, queryDTO.getMaterialCode())
               .like(queryDTO.getMaterialName() != null, ErpMaterial::getMaterialName, queryDTO.getMaterialName())
               .eq(queryDTO.getMaterialType() != null, ErpMaterial::getMaterialType, queryDTO.getMaterialType())
               .eq(queryDTO.getCategory() != null, ErpMaterial::getCategory, queryDTO.getCategory())
               .eq(queryDTO.getStatus() != null, ErpMaterial::getStatus, queryDTO.getStatus())
               .orderByDesc(ErpMaterial::getCreateTime);
        
        Page<ErpMaterial> result = materialMapper.selectPage(page, wrapper);
        
        List<MaterialVO> voList = result.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        return PageResult.of(
            result.getTotal(), 
            voList, 
            (long) result.getCurrent(), 
            (long) result.getSize()
        );
    }

    /**
     * 根据ID查询物料
     */
    public MaterialVO getMaterialById(Long id) {
        ErpMaterial material = materialMapper.selectById(id);
        if (material == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return convertToVO(material);
    }

    /**
     * 创建物料
     */
    @Transactional(rollbackFor = Exception.class)
    public void createMaterial(MaterialCreateDTO createDTO) {
        Long count = materialMapper.selectCount(
            new LambdaQueryWrapper<ErpMaterial>()
                .eq(ErpMaterial::getMaterialCode, createDTO.getMaterialCode())
        );
        if (count > 0) {
            throw new BusinessException(ResultCode.PRODUCT_CODE_EXISTS, "物料编码已存在");
        }

        ErpMaterial material = new ErpMaterial();
        material.setMaterialCode(createDTO.getMaterialCode());
        material.setMaterialName(createDTO.getMaterialName());
        material.setMaterialType(createDTO.getMaterialType());
        material.setSpecification(createDTO.getSpecification());
        material.setUnit(createDTO.getUnit());
        material.setCategory(createDTO.getCategory());
        material.setBrand(createDTO.getBrand());
        material.setCustomAttributes(createDTO.getCustomAttributes());
        material.setDescription(createDTO.getDescription());
        material.setStatus(createDTO.getStatus());
        
        materialMapper.insert(material);
    }

    /**
     * 更新物料
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateMaterial(MaterialUpdateDTO updateDTO) {
        ErpMaterial material = materialMapper.selectById(updateDTO.getId());
        if (material == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        if (updateDTO.getMaterialName() != null) {
            material.setMaterialName(updateDTO.getMaterialName());
        }
        if (updateDTO.getMaterialType() != null) {
            material.setMaterialType(updateDTO.getMaterialType());
        }
        if (updateDTO.getSpecification() != null) {
            material.setSpecification(updateDTO.getSpecification());
        }
        if (updateDTO.getUnit() != null) {
            material.setUnit(updateDTO.getUnit());
        }
        if (updateDTO.getCategory() != null) {
            material.setCategory(updateDTO.getCategory());
        }
        if (updateDTO.getBrand() != null) {
            material.setBrand(updateDTO.getBrand());
        }
        if (updateDTO.getCustomAttributes() != null) {
            material.setCustomAttributes(updateDTO.getCustomAttributes());
        }
        if (updateDTO.getDescription() != null) {
            material.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getStatus() != null) {
            material.setStatus(updateDTO.getStatus());
        }
        
        materialMapper.updateById(material);
    }

    /**
     * 删除物料（软删除）
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteMaterial(Long id) {
        ErpMaterial material = materialMapper.selectById(id);
        if (material == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        materialMapper.deleteById(id);
    }

    /**
     * 更新物料状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateMaterialStatus(Long id, Integer status) {
        ErpMaterial material = materialMapper.selectById(id);
        if (material == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        material.setStatus(status);
        materialMapper.updateById(material);
    }

    /**
     * 转换为VO
     */
    private MaterialVO convertToVO(ErpMaterial material) {
        return MaterialVO.builder()
            .id(material.getId())
            .materialCode(material.getMaterialCode())
            .materialName(material.getMaterialName())
            .materialType(material.getMaterialType())
            .materialTypeName(getMaterialTypeName(material.getMaterialType()))
            .specification(material.getSpecification())
            .unit(material.getUnit())
            .category(material.getCategory())
            .brand(material.getBrand())
            .customAttributes(material.getCustomAttributes())
            .description(material.getDescription())
            .status(material.getStatus())
            .createBy(material.getCreateBy())
            .createTime(material.getCreateTime())
            .updateBy(material.getUpdateBy())
            .updateTime(material.getUpdateTime())
            .build();
    }
}
