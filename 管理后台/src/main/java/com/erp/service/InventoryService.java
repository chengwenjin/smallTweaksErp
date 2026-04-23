package com.erp.service;

import com.baserbac.common.enums.ResultCode;
import com.baserbac.common.exception.BusinessException;
import com.baserbac.common.result.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.dto.InventoryCreateDTO;
import com.erp.dto.InventoryQueryDTO;
import com.erp.dto.InventoryUpdateDTO;
import com.erp.entity.ErpInventory;
import com.erp.mapper.InventoryMapper;
import com.erp.vo.InventoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryMapper inventoryMapper;

    public PageResult<InventoryVO> pageInventories(InventoryQueryDTO queryDTO) {
        Page<ErpInventory> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<ErpInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(queryDTO.getProductCode() != null, ErpInventory::getProductCode, queryDTO.getProductCode())
               .like(queryDTO.getProductName() != null, ErpInventory::getProductName, queryDTO.getProductName())
               .like(queryDTO.getWarehouseCode() != null, ErpInventory::getWarehouseCode, queryDTO.getWarehouseCode())
               .like(queryDTO.getWarehouseName() != null, ErpInventory::getWarehouseName, queryDTO.getWarehouseName())
               .eq(queryDTO.getStatus() != null, ErpInventory::getStatus, queryDTO.getStatus())
               .orderByDesc(ErpInventory::getCreateTime);
        
        Page<ErpInventory> result = inventoryMapper.selectPage(page, wrapper);
        
        List<InventoryVO> voList = result.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        return PageResult.of(
            result.getTotal(), 
            voList, 
            (long) result.getCurrent(), 
            (long) result.getSize()
        );
    }

    public InventoryVO getInventoryById(Long id) {
        ErpInventory inventory = inventoryMapper.selectById(id);
        if (inventory == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return convertToVO(inventory);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createInventory(InventoryCreateDTO createDTO) {
        ErpInventory inventory = new ErpInventory();
        inventory.setProductId(createDTO.getProductId());
        inventory.setProductCode(createDTO.getProductCode());
        inventory.setProductName(createDTO.getProductName());
        inventory.setSpecification(createDTO.getSpecification());
        inventory.setUnit(createDTO.getUnit());
        inventory.setWarehouseCode(createDTO.getWarehouseCode());
        inventory.setWarehouseName(createDTO.getWarehouseName());
        inventory.setLocationCode(createDTO.getLocationCode());
        inventory.setQuantity(createDTO.getQuantity() != null ? createDTO.getQuantity() : BigDecimal.ZERO);
        inventory.setLockedQuantity(createDTO.getLockedQuantity() != null ? createDTO.getLockedQuantity() : BigDecimal.ZERO);
        inventory.setAvailableQuantity(calculateAvailableQuantity(inventory.getQuantity(), inventory.getLockedQuantity()));
        inventory.setSafetyStock(createDTO.getSafetyStock() != null ? createDTO.getSafetyStock() : BigDecimal.ZERO);
        inventory.setBatchNo(createDTO.getBatchNo());
        inventory.setProductionDate(createDTO.getProductionDate());
        inventory.setExpiryDate(createDTO.getExpiryDate());
        inventory.setRemark(createDTO.getRemark());
        inventory.setStatus(createDTO.getStatus());
        
        inventoryMapper.insert(inventory);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateInventory(InventoryUpdateDTO updateDTO) {
        ErpInventory inventory = inventoryMapper.selectById(updateDTO.getId());
        if (inventory == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        if (updateDTO.getWarehouseCode() != null) {
            inventory.setWarehouseCode(updateDTO.getWarehouseCode());
        }
        if (updateDTO.getWarehouseName() != null) {
            inventory.setWarehouseName(updateDTO.getWarehouseName());
        }
        if (updateDTO.getLocationCode() != null) {
            inventory.setLocationCode(updateDTO.getLocationCode());
        }
        if (updateDTO.getQuantity() != null) {
            inventory.setQuantity(updateDTO.getQuantity());
        }
        if (updateDTO.getLockedQuantity() != null) {
            inventory.setLockedQuantity(updateDTO.getLockedQuantity());
        }
        if (updateDTO.getSafetyStock() != null) {
            inventory.setSafetyStock(updateDTO.getSafetyStock());
        }
        if (updateDTO.getBatchNo() != null) {
            inventory.setBatchNo(updateDTO.getBatchNo());
        }
        if (updateDTO.getProductionDate() != null) {
            inventory.setProductionDate(updateDTO.getProductionDate());
        }
        if (updateDTO.getExpiryDate() != null) {
            inventory.setExpiryDate(updateDTO.getExpiryDate());
        }
        if (updateDTO.getRemark() != null) {
            inventory.setRemark(updateDTO.getRemark());
        }
        if (updateDTO.getStatus() != null) {
            inventory.setStatus(updateDTO.getStatus());
        }
        
        inventory.setAvailableQuantity(calculateAvailableQuantity(inventory.getQuantity(), inventory.getLockedQuantity()));
        
        inventoryMapper.updateById(inventory);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteInventory(Long id) {
        ErpInventory inventory = inventoryMapper.selectById(id);
        if (inventory == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        inventoryMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateInventoryStatus(Long id, Integer status) {
        ErpInventory inventory = inventoryMapper.selectById(id);
        if (inventory == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        inventory.setStatus(status);
        inventoryMapper.updateById(inventory);
    }

    private BigDecimal calculateAvailableQuantity(BigDecimal quantity, BigDecimal lockedQuantity) {
        if (quantity == null) quantity = BigDecimal.ZERO;
        if (lockedQuantity == null) lockedQuantity = BigDecimal.ZERO;
        return quantity.subtract(lockedQuantity);
    }

    private InventoryVO convertToVO(ErpInventory inventory) {
        String statusName = switch (inventory.getStatus()) {
            case 1 -> "正常";
            case 2 -> "冻结";
            default -> "未知状态";
        };
        
        return InventoryVO.builder()
            .id(inventory.getId())
            .productId(inventory.getProductId())
            .productCode(inventory.getProductCode())
            .productName(inventory.getProductName())
            .specification(inventory.getSpecification())
            .unit(inventory.getUnit())
            .warehouseCode(inventory.getWarehouseCode())
            .warehouseName(inventory.getWarehouseName())
            .locationCode(inventory.getLocationCode())
            .quantity(inventory.getQuantity())
            .lockedQuantity(inventory.getLockedQuantity())
            .availableQuantity(inventory.getAvailableQuantity())
            .safetyStock(inventory.getSafetyStock())
            .batchNo(inventory.getBatchNo())
            .productionDate(inventory.getProductionDate())
            .expiryDate(inventory.getExpiryDate())
            .remark(inventory.getRemark())
            .status(inventory.getStatus())
            .statusName(statusName)
            .createBy(inventory.getCreateBy())
            .createTime(inventory.getCreateTime())
            .updateBy(inventory.getUpdateBy())
            .updateTime(inventory.getUpdateTime())
            .build();
    }
}
