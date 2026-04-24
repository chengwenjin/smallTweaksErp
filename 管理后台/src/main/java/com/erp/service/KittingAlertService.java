package com.erp.service;

import com.baserbac.common.enums.ResultCode;
import com.baserbac.common.exception.BusinessException;
import com.baserbac.common.result.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.dto.KittingAlertCreateDTO;
import com.erp.dto.KittingAlertQueryDTO;
import com.erp.dto.KittingAlertUpdateDTO;
import com.erp.entity.*;
import com.erp.mapper.*;
import com.erp.vo.KittingAlertVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KittingAlertService {

    private final KittingAlertMapper kittingAlertMapper;
    private final MpsMapper mpsMapper;
    private final BomMapper bomMapper;
    private final BomVersionMapper bomVersionMapper;
    private final InventoryMapper inventoryMapper;
    private final MaterialMapper materialMapper;
    private final ProductMapper productMapper;

    public PageResult<KittingAlertVO> pageKittingAlerts(KittingAlertQueryDTO queryDTO) {
        Page<ErpKittingAlert> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<ErpKittingAlert> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(queryDTO.getAlertNo() != null, ErpKittingAlert::getAlertNo, queryDTO.getAlertNo())
               .like(queryDTO.getMpsNo() != null, ErpKittingAlert::getMpsNo, queryDTO.getMpsNo())
               .like(queryDTO.getProductCode() != null, ErpKittingAlert::getProductCode, queryDTO.getProductCode())
               .like(queryDTO.getProductName() != null, ErpKittingAlert::getProductName, queryDTO.getProductName())
               .like(queryDTO.getMaterialCode() != null, ErpKittingAlert::getMaterialCode, queryDTO.getMaterialCode())
               .like(queryDTO.getMaterialName() != null, ErpKittingAlert::getMaterialName, queryDTO.getMaterialName())
               .eq(queryDTO.getAlertLevel() != null, ErpKittingAlert::getAlertLevel, queryDTO.getAlertLevel())
               .eq(queryDTO.getRequiredDate() != null, ErpKittingAlert::getRequiredDate, queryDTO.getRequiredDate())
               .eq(queryDTO.getSyncedToScrm() != null, ErpKittingAlert::getSyncedToScrm, queryDTO.getSyncedToScrm())
               .eq(queryDTO.getStatus() != null, ErpKittingAlert::getStatus, queryDTO.getStatus())
               .orderByAsc(ErpKittingAlert::getAlertLevel)
               .orderByDesc(ErpKittingAlert::getCreateTime);
        
        Page<ErpKittingAlert> result = kittingAlertMapper.selectPage(page, wrapper);
        
        List<KittingAlertVO> voList = result.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        return PageResult.of(
            result.getTotal(), 
            voList, 
            (long) result.getCurrent(), 
            (long) result.getSize()
        );
    }

    public KittingAlertVO getKittingAlertById(Long id) {
        ErpKittingAlert alert = kittingAlertMapper.selectById(id);
        if (alert == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return convertToVO(alert);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createKittingAlert(KittingAlertCreateDTO createDTO) {
        if (createDTO.getAlertNo() == null || createDTO.getAlertNo().isEmpty()) {
            String alertNo = "KA" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + 
                String.format("%06d", System.currentTimeMillis() % 1000000);
            createDTO.setAlertNo(alertNo);
        }

        ErpKittingAlert alert = new ErpKittingAlert();
        alert.setAlertNo(createDTO.getAlertNo());
        alert.setMpsId(createDTO.getMpsId());
        alert.setMpsNo(createDTO.getMpsNo());
        alert.setProductId(createDTO.getProductId());
        alert.setProductCode(createDTO.getProductCode());
        alert.setProductName(createDTO.getProductName());
        alert.setMaterialId(createDTO.getMaterialId());
        alert.setMaterialCode(createDTO.getMaterialCode());
        alert.setMaterialName(createDTO.getMaterialName());
        alert.setSpecification(createDTO.getSpecification());
        alert.setUnit(createDTO.getUnit());
        alert.setRequiredQuantity(createDTO.getRequiredQuantity());
        alert.setStockQuantity(createDTO.getStockQuantity());
        alert.setAllocatedQuantity(createDTO.getAllocatedQuantity());
        alert.setShortageQuantity(createDTO.getShortageQuantity());
        alert.setKittingRate(createDTO.getKittingRate());
        alert.setAlertLevel(createDTO.getAlertLevel());
        alert.setRequiredDate(createDTO.getRequiredDate());
        alert.setExpectedArrivalDate(createDTO.getExpectedArrivalDate());
        alert.setSupplierCode(createDTO.getSupplierCode());
        alert.setSupplierName(createDTO.getSupplierName());
        alert.setPurchaseOrderNo(createDTO.getPurchaseOrderNo());
        alert.setSyncedToScrm(0);
        alert.setRemark(createDTO.getRemark());
        alert.setStatus(createDTO.getStatus());
        
        kittingAlertMapper.insert(alert);
        log.info("创建齐套预警成功: {}", createDTO.getAlertNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateKittingAlert(KittingAlertUpdateDTO updateDTO) {
        ErpKittingAlert alert = kittingAlertMapper.selectById(updateDTO.getId());
        if (alert == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        if (updateDTO.getExpectedArrivalDate() != null) {
            alert.setExpectedArrivalDate(updateDTO.getExpectedArrivalDate());
        }
        if (updateDTO.getSupplierCode() != null) {
            alert.setSupplierCode(updateDTO.getSupplierCode());
        }
        if (updateDTO.getSupplierName() != null) {
            alert.setSupplierName(updateDTO.getSupplierName());
        }
        if (updateDTO.getPurchaseOrderNo() != null) {
            alert.setPurchaseOrderNo(updateDTO.getPurchaseOrderNo());
        }
        if (updateDTO.getRemark() != null) {
            alert.setRemark(updateDTO.getRemark());
        }
        if (updateDTO.getStatus() != null) {
            alert.setStatus(updateDTO.getStatus());
        }
        
        kittingAlertMapper.updateById(alert);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteKittingAlert(Long id) {
        ErpKittingAlert alert = kittingAlertMapper.selectById(id);
        if (alert == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        kittingAlertMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateKittingAlertStatus(Long id, Integer status) {
        ErpKittingAlert alert = kittingAlertMapper.selectById(id);
        if (alert == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        alert.setStatus(status);
        kittingAlertMapper.updateById(alert);
    }

    @Transactional(rollbackFor = Exception.class)
    public void calculateKittingAlerts() {
        log.info("开始计算齐套预警...");
        
        List<ErpMps> mpsList = mpsMapper.selectList(
            new LambdaQueryWrapper<ErpMps>()
                .in(ErpMps::getStatus, 1, 2, 3)
                .orderByAsc(ErpMps::getPriority)
                .orderByAsc(ErpMps::getPlanStartDate)
        );
        
        for (ErpMps mps : mpsList) {
            if (mps.getProductId() == null || mps.getPlannedQuantity() == null) {
                continue;
            }
            
            List<ErpBom> bomList = bomMapper.selectList(
                new LambdaQueryWrapper<ErpBom>()
                    .eq(ErpBom::getParentId, mps.getProductId())
                    .eq(ErpBom::getParentType, 1)
                    .eq(ErpBom::getStatus, 1)
            );
            
            if (bomList.isEmpty()) {
                log.warn("产品 {} 没有找到BOM结构，跳过齐套计算", mps.getProductCode());
                continue;
            }
            
            for (ErpBom bom : bomList) {
                Long count = kittingAlertMapper.selectCount(
                    new LambdaQueryWrapper<ErpKittingAlert>()
                        .eq(ErpKittingAlert::getMpsId, mps.getId())
                        .eq(ErpKittingAlert::getMaterialId, bom.getChildId())
                        .ne(ErpKittingAlert::getStatus, 3)
                );
                
                if (count > 0) {
                    continue;
                }
                
                ErpMaterial material = null;
                ErpProduct product = null;
                
                if (bom.getChildType() == 2) {
                    material = materialMapper.selectById(bom.getChildId());
                } else {
                    product = productMapper.selectById(bom.getChildId());
                }
                
                BigDecimal requiredQuantity = bom.getQuantity().multiply(mps.getPlannedQuantity());
                
                List<ErpInventory> inventories = inventoryMapper.selectList(
                    new LambdaQueryWrapper<ErpInventory>()
                        .eq(ErpInventory::getProductId, bom.getChildId())
                );
                
                BigDecimal stockQuantity = BigDecimal.ZERO;
                BigDecimal lockedQuantity = BigDecimal.ZERO;
                
                for (ErpInventory inv : inventories) {
                    if (inv.getQuantity() != null) {
                        stockQuantity = stockQuantity.add(inv.getQuantity());
                    }
                    if (inv.getLockedQuantity() != null) {
                        lockedQuantity = lockedQuantity.add(inv.getLockedQuantity());
                    }
                }
                
                BigDecimal availableQuantity = stockQuantity.subtract(lockedQuantity);
                BigDecimal shortageQuantity = requiredQuantity.subtract(availableQuantity);
                
                if (shortageQuantity.compareTo(BigDecimal.ZERO) <= 0) {
                    log.info("物料 {} 库存充足，需求: {}, 可用: {}", 
                        material != null ? material.getMaterialCode() : (product != null ? product.getProductCode() : "未知"),
                        requiredQuantity, availableQuantity);
                    continue;
                }
                
                BigDecimal kittingRate = availableQuantity.divide(requiredQuantity, 2, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100));
                
                int alertLevel;
                if (kittingRate.compareTo(BigDecimal.ZERO) == 0) {
                    alertLevel = 1;
                } else if (kittingRate.compareTo(new BigDecimal(30)) < 0) {
                    alertLevel = 2;
                } else if (kittingRate.compareTo(new BigDecimal(60)) < 0) {
                    alertLevel = 3;
                } else {
                    alertLevel = 4;
                }
                
                ErpKittingAlert alert = new ErpKittingAlert();
                String alertNo = "KA" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + 
                    String.format("%06d", System.currentTimeMillis() % 1000000);
                alert.setAlertNo(alertNo);
                alert.setMpsId(mps.getId());
                alert.setMpsNo(mps.getMpsNo());
                alert.setProductId(mps.getProductId());
                alert.setProductCode(mps.getProductCode());
                alert.setProductName(mps.getProductName());
                
                if (material != null) {
                    alert.setMaterialId(material.getId());
                    alert.setMaterialCode(material.getMaterialCode());
                    alert.setMaterialName(material.getMaterialName());
                    alert.setSpecification(material.getSpecification());
                    alert.setUnit(material.getUnit());
                } else if (product != null) {
                    alert.setMaterialId(product.getId());
                    alert.setMaterialCode(product.getProductCode());
                    alert.setMaterialName(product.getProductName());
                    alert.setSpecification(product.getSpecification());
                    alert.setUnit(product.getUnit());
                }
                
                alert.setRequiredQuantity(requiredQuantity);
                alert.setStockQuantity(stockQuantity);
                alert.setAllocatedQuantity(lockedQuantity);
                alert.setShortageQuantity(shortageQuantity);
                alert.setKittingRate(kittingRate);
                alert.setAlertLevel(alertLevel);
                alert.setRequiredDate(mps.getPlanStartDate());
                alert.setSyncedToScrm(0);
                alert.setRemark("齐套预警: 齐套率 " + kittingRate + "%");
                alert.setStatus(1);
                
                kittingAlertMapper.insert(alert);
                log.info("生成齐套预警: {} - 物料: {} - 缺口: {} - 齐套率: {}%", 
                    alertNo, alert.getMaterialCode(), shortageQuantity, kittingRate);
            }
        }
        
        log.info("齐套预警计算完成");
    }

    @Transactional(rollbackFor = Exception.class)
    public void syncToScrm(Long id) {
        ErpKittingAlert alert = kittingAlertMapper.selectById(id);
        if (alert == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        log.info("同步齐套预警 {} 到SCM模块", alert.getAlertNo());
        
        alert.setSyncedToScrm(1);
        alert.setSyncedTime(LocalDateTime.now());
        alert.setStatus(2);
        
        kittingAlertMapper.updateById(alert);
        log.info("齐套预警 {} 已同步到SCM模块", alert.getAlertNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void syncToScrmBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        
        for (Long id : ids) {
            syncToScrm(id);
        }
    }

    private KittingAlertVO convertToVO(ErpKittingAlert alert) {
        String alertLevelName = switch (alert.getAlertLevel()) {
            case 1 -> "紧急";
            case 2 -> "高";
            case 3 -> "中";
            case 4 -> "低";
            default -> "未知";
        };
        
        String statusName = switch (alert.getStatus()) {
            case 1 -> "待处理";
            case 2 -> "已跟催";
            case 3 -> "已解决";
            case 4 -> "已忽略";
            default -> "未知";
        };
        
        return KittingAlertVO.builder()
            .id(alert.getId())
            .alertNo(alert.getAlertNo())
            .mpsId(alert.getMpsId())
            .mpsNo(alert.getMpsNo())
            .productId(alert.getProductId())
            .productCode(alert.getProductCode())
            .productName(alert.getProductName())
            .materialId(alert.getMaterialId())
            .materialCode(alert.getMaterialCode())
            .materialName(alert.getMaterialName())
            .specification(alert.getSpecification())
            .unit(alert.getUnit())
            .requiredQuantity(alert.getRequiredQuantity())
            .stockQuantity(alert.getStockQuantity())
            .allocatedQuantity(alert.getAllocatedQuantity())
            .shortageQuantity(alert.getShortageQuantity())
            .kittingRate(alert.getKittingRate())
            .alertLevel(alert.getAlertLevel())
            .alertLevelName(alertLevelName)
            .requiredDate(alert.getRequiredDate())
            .expectedArrivalDate(alert.getExpectedArrivalDate())
            .supplierCode(alert.getSupplierCode())
            .supplierName(alert.getSupplierName())
            .purchaseOrderNo(alert.getPurchaseOrderNo())
            .syncedToScrm(alert.getSyncedToScrm())
            .syncedTime(alert.getSyncedTime())
            .remark(alert.getRemark())
            .status(alert.getStatus())
            .statusName(statusName)
            .createBy(alert.getCreateBy())
            .createTime(alert.getCreateTime())
            .updateBy(alert.getUpdateBy())
            .updateTime(alert.getUpdateTime())
            .build();
    }
}
