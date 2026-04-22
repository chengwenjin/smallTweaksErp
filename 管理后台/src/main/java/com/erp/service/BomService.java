package com.erp.service;

import com.baserbac.common.enums.ResultCode;
import com.baserbac.common.exception.BusinessException;
import com.baserbac.common.result.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.dto.BomCreateDTO;
import com.erp.dto.BomQueryDTO;
import com.erp.dto.BomUpdateDTO;
import com.erp.entity.ErpBom;
import com.erp.entity.ErpMaterial;
import com.erp.entity.ErpProduct;
import com.erp.mapper.BomMapper;
import com.erp.mapper.MaterialMapper;
import com.erp.mapper.ProductMapper;
import com.erp.vo.BomTreeVO;
import com.erp.vo.BomVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * BOM服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BomService {

    private final BomMapper bomMapper;
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
     * 分页查询BOM列表
     */
    public PageResult<BomVO> pageBoms(BomQueryDTO queryDTO) {
        Page<ErpBom> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<ErpBom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryDTO.getParentId() != null, ErpBom::getParentId, queryDTO.getParentId())
               .eq(queryDTO.getParentType() != null, ErpBom::getParentType, queryDTO.getParentType())
               .eq(queryDTO.getChildId() != null, ErpBom::getChildId, queryDTO.getChildId())
               .eq(queryDTO.getChildType() != null, ErpBom::getChildType, queryDTO.getChildType())
               .eq(queryDTO.getStatus() != null, ErpBom::getStatus, queryDTO.getStatus())
               .orderByDesc(ErpBom::getCreateTime);
        
        Page<ErpBom> result = bomMapper.selectPage(page, wrapper);
        
        List<BomVO> voList = result.getRecords().stream()
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
     * 根据ID查询BOM
     */
    public BomVO getBomById(Long id) {
        ErpBom bom = bomMapper.selectById(id);
        if (bom == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return convertToVO(bom);
    }

    /**
     * 创建BOM
     */
    @Transactional(rollbackFor = Exception.class)
    public void createBom(BomCreateDTO createDTO) {
        // 检查父件是否存在
        if (createDTO.getParentType() == 1) {
            ErpProduct product = productMapper.selectById(createDTO.getParentId());
            if (product == null) {
                throw new BusinessException(ResultCode.NOT_FOUND, "父件产品不存在");
            }
        } else if (createDTO.getParentType() == 2) {
            ErpMaterial material = materialMapper.selectById(createDTO.getParentId());
            if (material == null) {
                throw new BusinessException(ResultCode.NOT_FOUND, "父件物料不存在");
            }
        }

        // 检查子件是否存在
        if (createDTO.getChildType() == 1) {
            ErpProduct product = productMapper.selectById(createDTO.getChildId());
            if (product == null) {
                throw new BusinessException(ResultCode.NOT_FOUND, "子件产品不存在");
            }
        } else if (createDTO.getChildType() == 2) {
            ErpMaterial material = materialMapper.selectById(createDTO.getChildId());
            if (material == null) {
                throw new BusinessException(ResultCode.NOT_FOUND, "子件物料不存在");
            }
        }

        // 检查是否已存在相同的BOM关系
        Long count = bomMapper.selectCount(
            new LambdaQueryWrapper<ErpBom>()
                .eq(ErpBom::getParentId, createDTO.getParentId())
                .eq(ErpBom::getParentType, createDTO.getParentType())
                .eq(ErpBom::getChildId, createDTO.getChildId())
                .eq(ErpBom::getChildType, createDTO.getChildType())
        );
        if (count > 0) {
            throw new BusinessException(ResultCode.BOM_RELATION_EXISTS, "该BOM关系已存在");
        }

        ErpBom bom = new ErpBom();
        bom.setParentId(createDTO.getParentId());
        bom.setParentType(createDTO.getParentType());
        bom.setChildId(createDTO.getChildId());
        bom.setChildType(createDTO.getChildType());
        bom.setQuantity(createDTO.getQuantity());
        bom.setUnit(createDTO.getUnit());
        bom.setRemark(createDTO.getRemark());
        bom.setStatus(createDTO.getStatus());
        
        bomMapper.insert(bom);
    }

    /**
     * 更新BOM
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateBom(BomUpdateDTO updateDTO) {
        ErpBom bom = bomMapper.selectById(updateDTO.getId());
        if (bom == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        if (updateDTO.getQuantity() != null) {
            bom.setQuantity(updateDTO.getQuantity());
        }
        if (updateDTO.getUnit() != null) {
            bom.setUnit(updateDTO.getUnit());
        }
        if (updateDTO.getRemark() != null) {
            bom.setRemark(updateDTO.getRemark());
        }
        if (updateDTO.getStatus() != null) {
            bom.setStatus(updateDTO.getStatus());
        }
        
        bomMapper.updateById(bom);
    }

    /**
     * 删除BOM
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteBom(Long id) {
        ErpBom bom = bomMapper.selectById(id);
        if (bom == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        bomMapper.deleteById(id);
    }

    /**
     * 更新BOM状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateBomStatus(Long id, Integer status) {
        ErpBom bom = bomMapper.selectById(id);
        if (bom == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        bom.setStatus(status);
        bomMapper.updateById(bom);
    }

    /**
     * 获取BOM层级结构
     */
    public BomTreeVO getBomTree(Long parentId, Integer parentType) {
        BomTreeVO root = new BomTreeVO();
        root.setId(parentId);
        root.setType(parentType);
        root.setCode(getCodeByIdAndType(parentId, parentType));
        root.setName(getNameByIdAndType(parentId, parentType));
        root.setQuantity(BigDecimal.ONE);
        root.setUnit("");
        root.setChildren(buildBomTree(parentId, parentType));
        return root;
    }

    private List<BomTreeVO> buildBomTree(Long parentId, Integer parentType) {
        List<ErpBom> boms = bomMapper.selectList(
            new LambdaQueryWrapper<ErpBom>()
                .eq(ErpBom::getParentId, parentId)
                .eq(ErpBom::getParentType, parentType)
                .eq(ErpBom::getStatus, 1)
        );

        return boms.stream().map(bom -> {
            BomTreeVO node = new BomTreeVO();
            node.setId(bom.getChildId());
            node.setType(bom.getChildType());
            node.setCode(getCodeByIdAndType(bom.getChildId(), bom.getChildType()));
            node.setName(getNameByIdAndType(bom.getChildId(), bom.getChildType()));
            node.setQuantity(bom.getQuantity());
            node.setUnit(bom.getUnit());
            node.setChildren(buildBomTree(bom.getChildId(), bom.getChildType()));
            return node;
        }).collect(Collectors.toList());
    }

    /**
     * 转换为VO
     */
    private BomVO convertToVO(ErpBom bom) {
        return BomVO.builder()
            .id(bom.getId())
            .parentId(bom.getParentId())
            .parentType(bom.getParentType())
            .parentTypeName(getTypeName(bom.getParentType()))
            .parentCode(getCodeByIdAndType(bom.getParentId(), bom.getParentType()))
            .parentName(getNameByIdAndType(bom.getParentId(), bom.getParentType()))
            .childId(bom.getChildId())
            .childType(bom.getChildType())
            .childTypeName(getTypeName(bom.getChildType()))
            .childCode(getCodeByIdAndType(bom.getChildId(), bom.getChildType()))
            .childName(getNameByIdAndType(bom.getChildId(), bom.getChildType()))
            .quantity(bom.getQuantity())
            .unit(bom.getUnit())
            .remark(bom.getRemark())
            .status(bom.getStatus())
            .createBy(bom.getCreateBy())
            .createTime(bom.getCreateTime())
            .updateBy(bom.getUpdateBy())
            .updateTime(bom.getUpdateTime())
            .build();
    }
}
