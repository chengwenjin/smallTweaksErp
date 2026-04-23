package com.erp.service;

import com.baserbac.common.enums.ResultCode;
import com.baserbac.common.exception.BusinessException;
import com.baserbac.common.result.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.dto.NetRequirementQueryDTO;
import com.erp.entity.ErpDemandSource;
import com.erp.entity.ErpInventory;
import com.erp.entity.ErpNetRequirement;
import com.erp.mapper.DemandSourceMapper;
import com.erp.mapper.InventoryMapper;
import com.erp.mapper.NetRequirementMapper;
import com.erp.vo.NetRequirementVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NetRequirementService {

    private final NetRequirementMapper netRequirementMapper;
    private final DemandSourceMapper demandSourceMapper;
    private final InventoryMapper inventoryMapper;

    public PageResult<NetRequirementVO> pageNetRequirements(NetRequirementQueryDTO queryDTO) {
        Page<ErpNetRequirement> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<ErpNetRequirement> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(queryDTO.getPlanNo() != null, ErpNetRequirement::getPlanNo, queryDTO.getPlanNo())
               .like(queryDTO.getProductCode() != null, ErpNetRequirement::getProductCode, queryDTO.getProductCode())
               .like(queryDTO.getProductName() != null, ErpNetRequirement::getProductName, queryDTO.getProductName())
               .eq(queryDTO.getStatus() != null, ErpNetRequirement::getStatus, queryDTO.getStatus())
               .orderByAsc(ErpNetRequirement::getRequirementDate)
               .orderByDesc(ErpNetRequirement::getCreateTime);
        
        Page<ErpNetRequirement> result = netRequirementMapper.selectPage(page, wrapper);
        
        List<NetRequirementVO> voList = result.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        return PageResult.of(
            result.getTotal(), 
            voList, 
            (long) result.getCurrent(), 
            (long) result.getSize()
        );
    }

    public NetRequirementVO getNetRequirementById(Long id) {
        ErpNetRequirement netRequirement = netRequirementMapper.selectById(id);
        if (netRequirement == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return convertToVO(netRequirement);
    }

    @Transactional(rollbackFor = Exception.class)
    public void calculateNetRequirements() {
        log.info("开始计算净需求...");
        
        LambdaQueryWrapper<ErpDemandSource> demandWrapper = new LambdaQueryWrapper<>();
        demandWrapper.eq(ErpDemandSource::getStatus, 1)
                    .orderByAsc(ErpDemandSource::getDemandDate);
        
        List<ErpDemandSource> demandSources = demandSourceMapper.selectList(demandWrapper);
        
        Map<Long, NetRequirementCalc> productReqs = new HashMap<>();
        
        for (ErpDemandSource demand : demandSources) {
            Long productId = demand.getProductId();
            NetRequirementCalc calc = productReqs.computeIfAbsent(productId, k -> new NetRequirementCalc());
            
            calc.productId = productId;
            calc.productCode = demand.getProductCode();
            calc.productName = demand.getProductName();
            calc.specification = demand.getSpecification();
            calc.unit = demand.getUnit();
            calc.demandDate = demand.getDemandDate();
            calc.grossDemand = calc.grossDemand.add(demand.getDemandQuantity());
        }
        
        for (Map.Entry<Long, NetRequirementCalc> entry : productReqs.entrySet()) {
            Long productId = entry.getKey();
            NetRequirementCalc calc = entry.getValue();
            
            LambdaQueryWrapper<ErpInventory> inventoryWrapper = new LambdaQueryWrapper<>();
            inventoryWrapper.eq(ErpInventory::getProductId, productId)
                           .eq(ErpInventory::getStatus, 1);
            
            List<ErpInventory> inventories = inventoryMapper.selectList(inventoryWrapper);
            
            BigDecimal totalStock = BigDecimal.ZERO;
            BigDecimal totalLocked = BigDecimal.ZERO;
            BigDecimal totalSafetyStock = BigDecimal.ZERO;
            
            for (ErpInventory inv : inventories) {
                if (inv.getQuantity() != null) totalStock = totalStock.add(inv.getQuantity());
                if (inv.getLockedQuantity() != null) totalLocked = totalLocked.add(inv.getLockedQuantity());
                if (inv.getSafetyStock() != null) totalSafetyStock = totalSafetyStock.add(inv.getSafetyStock());
            }
            
            BigDecimal availableStock = totalStock.subtract(totalLocked).subtract(totalSafetyStock);
            if (availableStock.compareTo(BigDecimal.ZERO) < 0) {
                availableStock = BigDecimal.ZERO;
            }
            
            BigDecimal netReq = calc.grossDemand.subtract(availableStock);
            if (netReq.compareTo(BigDecimal.ZERO) < 0) {
                netReq = BigDecimal.ZERO;
            }
            
            if (netReq.compareTo(BigDecimal.ZERO) > 0) {
                LambdaQueryWrapper<ErpNetRequirement> checkWrapper = new LambdaQueryWrapper<>();
                checkWrapper.eq(ErpNetRequirement::getProductId, productId)
                           .eq(ErpNetRequirement::getRequirementDate, calc.demandDate)
                           .eq(ErpNetRequirement::getStatus, 1);
                
                Long count = netRequirementMapper.selectCount(checkWrapper);
                
                if (count == 0) {
                    ErpNetRequirement netRequirement = new ErpNetRequirement();
                    String planNo = "NR" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + 
                        String.format("%06d", System.currentTimeMillis() % 1000000);
                    netRequirement.setPlanNo(planNo);
                    netRequirement.setProductId(calc.productId);
                    netRequirement.setProductCode(calc.productCode);
                    netRequirement.setProductName(calc.productName);
                    netRequirement.setSpecification(calc.specification);
                    netRequirement.setUnit(calc.unit);
                    netRequirement.setRequirementDate(calc.demandDate);
                    netRequirement.setGrossDemand(calc.grossDemand);
                    netRequirement.setStockQuantity(totalStock);
                    netRequirement.setLockedQuantity(totalLocked);
                    netRequirement.setSafetyStock(totalSafetyStock);
                    netRequirement.setAvailableQuantity(availableStock);
                    netRequirement.setNetRequirement(netReq);
                    netRequirement.setPlannedReceipt(BigDecimal.ZERO);
                    netRequirement.setPlannedOrder(netReq);
                    netRequirement.setStatus(1);
                    netRequirement.setRemark("净需求计算生成");
                    
                    netRequirementMapper.insert(netRequirement);
                    log.info("生成净需求计划: {} - {} - 毛需求: {}, 可用库存: {}, 净需求: {}", 
                        planNo, calc.productName, calc.grossDemand, availableStock, netReq);
                }
            }
        }
        
        log.info("净需求计算完成");
    }

    @Transactional(rollbackFor = Exception.class)
    public void confirmNetRequirement(Long id) {
        ErpNetRequirement netRequirement = netRequirementMapper.selectById(id);
        if (netRequirement == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        netRequirement.setStatus(2);
        netRequirementMapper.updateById(netRequirement);
        log.info("确认净需求计划: {}", netRequirement.getPlanNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateNetRequirementStatus(Long id, Integer status) {
        ErpNetRequirement netRequirement = netRequirementMapper.selectById(id);
        if (netRequirement == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        netRequirement.setStatus(status);
        netRequirementMapper.updateById(netRequirement);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteNetRequirement(Long id) {
        ErpNetRequirement netRequirement = netRequirementMapper.selectById(id);
        if (netRequirement == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        netRequirementMapper.deleteById(id);
    }

    private NetRequirementVO convertToVO(ErpNetRequirement netRequirement) {
        String statusName = switch (netRequirement.getStatus()) {
            case 1 -> "待确认";
            case 2 -> "已确认";
            case 3 -> "已执行";
            default -> "未知状态";
        };
        
        return NetRequirementVO.builder()
            .id(netRequirement.getId())
            .planNo(netRequirement.getPlanNo())
            .productId(netRequirement.getProductId())
            .productCode(netRequirement.getProductCode())
            .productName(netRequirement.getProductName())
            .specification(netRequirement.getSpecification())
            .unit(netRequirement.getUnit())
            .requirementDate(netRequirement.getRequirementDate())
            .grossDemand(netRequirement.getGrossDemand())
            .stockQuantity(netRequirement.getStockQuantity())
            .lockedQuantity(netRequirement.getLockedQuantity())
            .safetyStock(netRequirement.getSafetyStock())
            .availableQuantity(netRequirement.getAvailableQuantity())
            .netRequirement(netRequirement.getNetRequirement())
            .plannedReceipt(netRequirement.getPlannedReceipt())
            .plannedOrder(netRequirement.getPlannedOrder())
            .remark(netRequirement.getRemark())
            .status(netRequirement.getStatus())
            .statusName(statusName)
            .createBy(netRequirement.getCreateBy())
            .createTime(netRequirement.getCreateTime())
            .updateBy(netRequirement.getUpdateBy())
            .updateTime(netRequirement.getUpdateTime())
            .build();
    }

    @lombok.Data
    private static class NetRequirementCalc {
        private Long productId;
        private String productCode;
        private String productName;
        private String specification;
        private String unit;
        private LocalDate demandDate;
        private BigDecimal grossDemand = BigDecimal.ZERO;
    }
}
