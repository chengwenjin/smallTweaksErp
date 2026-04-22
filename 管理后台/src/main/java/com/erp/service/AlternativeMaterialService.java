package com.erp.service;

import com.baserbac.common.enums.ResultCode;
import com.baserbac.common.exception.BusinessException;
import com.baserbac.common.result.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.dto.AlternativeMaterialCreateDTO;
import com.erp.dto.AlternativeMaterialQueryDTO;
import com.erp.dto.AlternativeMaterialUpdateDTO;
import com.erp.entity.ErpAlternativeMaterial;
import com.erp.entity.ErpMaterial;
import com.erp.entity.ErpProduct;
import com.erp.mapper.AlternativeMaterialMapper;
import com.erp.mapper.MaterialMapper;
import com.erp.mapper.ProductMapper;
import com.erp.vo.AlternativeMaterialVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 替代料服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlternativeMaterialService {

    private final AlternativeMaterialMapper alternativeMaterialMapper;
    private final ProductMapper productMapper;
    private final MaterialMapper materialMapper;

    private String getTypeName(Integer type) {
        if (type == null) {
            return "未知";
        }
        return switch (type) {
            case 1 -> "产品";
            case 2 -> "物料";
            default -> "未知";
        };
    }

    private String getCodeByIdAndType(Long id, Integer type) {
        if (id == null || type == null) {
            return "";
        }
        if (type == 1) {
            ErpProduct product = productMapper.selectById(id);
            return product != null ? product.getProductCode() : "";
        } else if (type == 2) {
            ErpMaterial material = materialMapper.selectById(id);
            return material != null ? material.getMaterialCode() : "";
        }
        return "";
    }

    private String getNameByIdAndType(Long id, Integer type) {
        if (id == null || type == null) {
            return "";
        }
        if (type == 1) {
            ErpProduct product = productMapper.selectById(id);
            return product != null ? product.getProductName() : "";
        } else if (type == 2) {
            ErpMaterial material = materialMapper.selectById(id);
            return material != null ? material.getMaterialName() : "";
        }
        return "";
    }

    /**
     * 分页查询替代料列表
     */
    public PageResult<AlternativeMaterialVO> pageAlternativeMaterials(AlternativeMaterialQueryDTO queryDTO) {
        Page<ErpAlternativeMaterial> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<ErpAlternativeMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryDTO.getMainMaterialId() != null, ErpAlternativeMaterial::getMainMaterialId, queryDTO.getMainMaterialId())
               .eq(queryDTO.getMainMaterialType() != null, ErpAlternativeMaterial::getMainMaterialType, queryDTO.getMainMaterialType())
               .eq(queryDTO.getAlternativeMaterialId() != null, ErpAlternativeMaterial::getAlternativeMaterialId, queryDTO.getAlternativeMaterialId())
               .eq(queryDTO.getAlternativeMaterialType() != null, ErpAlternativeMaterial::getAlternativeMaterialType, queryDTO.getAlternativeMaterialType())
               .eq(queryDTO.getStatus() != null, ErpAlternativeMaterial::getStatus, queryDTO.getStatus())
               .orderByAsc(ErpAlternativeMaterial::getPriority)
               .orderByDesc(ErpAlternativeMaterial::getCreateTime);
        
        Page<ErpAlternativeMaterial> result = alternativeMaterialMapper.selectPage(page, wrapper);
        
        List<AlternativeMaterialVO> voList = result.getRecords().stream()
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
     * 根据ID查询替代料
     */
    public AlternativeMaterialVO getAlternativeMaterialById(Long id) {
        ErpAlternativeMaterial material = alternativeMaterialMapper.selectById(id);
        if (material == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return convertToVO(material);
    }

    /**
     * 获取主料的替代料列表
     */
    public List<AlternativeMaterialVO> getAlternativeMaterialsByMainMaterial(Long mainMaterialId, Integer mainMaterialType) {
        List<ErpAlternativeMaterial> materials = alternativeMaterialMapper.selectList(
            new LambdaQueryWrapper<ErpAlternativeMaterial>()
                .eq(ErpAlternativeMaterial::getMainMaterialId, mainMaterialId)
                .eq(ErpAlternativeMaterial::getMainMaterialType, mainMaterialType)
                .eq(ErpAlternativeMaterial::getStatus, 1)
                .orderByAsc(ErpAlternativeMaterial::getPriority)
        );

        return materials.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    /**
     * 创建替代料
     */
    @Transactional(rollbackFor = Exception.class)
    public void createAlternativeMaterial(AlternativeMaterialCreateDTO createDTO) {
        // 检查主料是否存在
        if (createDTO.getMainMaterialType() == 1) {
            ErpProduct product = productMapper.selectById(createDTO.getMainMaterialId());
            if (product == null) {
                throw new BusinessException(ResultCode.NOT_FOUND, "主料产品不存在");
            }
        } else if (createDTO.getMainMaterialType() == 2) {
            ErpMaterial material = materialMapper.selectById(createDTO.getMainMaterialId());
            if (material == null) {
                throw new BusinessException(ResultCode.NOT_FOUND, "主料物料不存在");
            }
        }

        // 检查替代料是否存在
        if (createDTO.getAlternativeMaterialType() == 1) {
            ErpProduct product = productMapper.selectById(createDTO.getAlternativeMaterialId());
            if (product == null) {
                throw new BusinessException(ResultCode.NOT_FOUND, "替代料产品不存在");
            }
        } else if (createDTO.getAlternativeMaterialType() == 2) {
            ErpMaterial material = materialMapper.selectById(createDTO.getAlternativeMaterialId());
            if (material == null) {
                throw new BusinessException(ResultCode.NOT_FOUND, "替代料物料不存在");
            }
        }

        // 检查是否已存在相同的替代料关系
        Long count = alternativeMaterialMapper.selectCount(
            new LambdaQueryWrapper<ErpAlternativeMaterial>()
                .eq(ErpAlternativeMaterial::getMainMaterialId, createDTO.getMainMaterialId())
                .eq(ErpAlternativeMaterial::getMainMaterialType, createDTO.getMainMaterialType())
                .eq(ErpAlternativeMaterial::getAlternativeMaterialId, createDTO.getAlternativeMaterialId())
                .eq(ErpAlternativeMaterial::getAlternativeMaterialType, createDTO.getAlternativeMaterialType())
        );
        if (count > 0) {
            throw new BusinessException(ResultCode.ALTERNATIVE_MATERIAL_EXISTS, "该替代料关系已存在");
        }

        ErpAlternativeMaterial alternativeMaterial = new ErpAlternativeMaterial();
        alternativeMaterial.setMainMaterialId(createDTO.getMainMaterialId());
        alternativeMaterial.setMainMaterialType(createDTO.getMainMaterialType());
        alternativeMaterial.setAlternativeMaterialId(createDTO.getAlternativeMaterialId());
        alternativeMaterial.setAlternativeMaterialType(createDTO.getAlternativeMaterialType());
        alternativeMaterial.setAlternativeRatio(createDTO.getAlternativeRatio());
        alternativeMaterial.setPriority(createDTO.getPriority());
        alternativeMaterial.setApplicableScene(createDTO.getApplicableScene());
        alternativeMaterial.setRemark(createDTO.getRemark());
        alternativeMaterial.setStatus(createDTO.getStatus());
        
        alternativeMaterialMapper.insert(alternativeMaterial);
    }

    /**
     * 更新替代料
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateAlternativeMaterial(AlternativeMaterialUpdateDTO updateDTO) {
        ErpAlternativeMaterial material = alternativeMaterialMapper.selectById(updateDTO.getId());
        if (material == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        if (updateDTO.getAlternativeRatio() != null) {
            material.setAlternativeRatio(updateDTO.getAlternativeRatio());
        }
        if (updateDTO.getPriority() != null) {
            material.setPriority(updateDTO.getPriority());
        }
        if (updateDTO.getApplicableScene() != null) {
            material.setApplicableScene(updateDTO.getApplicableScene());
        }
        if (updateDTO.getRemark() != null) {
            material.setRemark(updateDTO.getRemark());
        }
        if (updateDTO.getStatus() != null) {
            material.setStatus(updateDTO.getStatus());
        }
        
        alternativeMaterialMapper.updateById(material);
    }

    /**
     * 删除替代料
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteAlternativeMaterial(Long id) {
        ErpAlternativeMaterial material = alternativeMaterialMapper.selectById(id);
        if (material == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        alternativeMaterialMapper.deleteById(id);
    }

    /**
     * 更新替代料状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateAlternativeMaterialStatus(Long id, Integer status) {
        ErpAlternativeMaterial material = alternativeMaterialMapper.selectById(id);
        if (material == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        material.setStatus(status);
        alternativeMaterialMapper.updateById(material);
    }

    /**
     * 转换为VO
     */
    private AlternativeMaterialVO convertToVO(ErpAlternativeMaterial material) {
        return AlternativeMaterialVO.builder()
            .id(material.getId())
            .mainMaterialId(material.getMainMaterialId())
            .mainMaterialType(material.getMainMaterialType())
            .mainMaterialTypeName(getTypeName(material.getMainMaterialType()))
            .mainMaterialCode(getCodeByIdAndType(material.getMainMaterialId(), material.getMainMaterialType()))
            .mainMaterialName(getNameByIdAndType(material.getMainMaterialId(), material.getMainMaterialType()))
            .alternativeMaterialId(material.getAlternativeMaterialId())
            .alternativeMaterialType(material.getAlternativeMaterialType())
            .alternativeMaterialTypeName(getTypeName(material.getAlternativeMaterialType()))
            .alternativeMaterialCode(getCodeByIdAndType(material.getAlternativeMaterialId(), material.getAlternativeMaterialType()))
            .alternativeMaterialName(getNameByIdAndType(material.getAlternativeMaterialId(), material.getAlternativeMaterialType()))
            .alternativeRatio(material.getAlternativeRatio())
            .priority(material.getPriority())
            .applicableScene(material.getApplicableScene())
            .remark(material.getRemark())
            .status(material.getStatus())
            .createBy(material.getCreateBy())
            .createTime(material.getCreateTime())
            .updateBy(material.getUpdateBy())
            .updateTime(material.getUpdateTime())
            .build();
    }
}
