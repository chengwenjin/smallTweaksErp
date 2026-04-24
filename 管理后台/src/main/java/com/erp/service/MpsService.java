package com.erp.service;

import com.baserbac.common.enums.ResultCode;
import com.baserbac.common.exception.BusinessException;
import com.baserbac.common.result.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.dto.MpsCreateDTO;
import com.erp.dto.MpsQueryDTO;
import com.erp.dto.MpsUpdateDTO;
import com.erp.entity.*;
import com.erp.mapper.*;
import com.erp.vo.MpsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpsService {

    private final MpsMapper mpsMapper;
    private final EquipmentMapper equipmentMapper;
    private final WorkGroupMapper workGroupMapper;
    private final NetRequirementMapper netRequirementMapper;
    private final EmployeeScheduleMapper employeeScheduleMapper;

    public PageResult<MpsVO> pageMps(MpsQueryDTO queryDTO) {
        Page<ErpMps> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<ErpMps> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(queryDTO.getMpsNo() != null, ErpMps::getMpsNo, queryDTO.getMpsNo())
               .like(queryDTO.getPlanName() != null, ErpMps::getPlanName, queryDTO.getPlanName())
               .like(queryDTO.getProductCode() != null, ErpMps::getProductCode, queryDTO.getProductCode())
               .like(queryDTO.getProductName() != null, ErpMps::getProductName, queryDTO.getProductName())
               .like(queryDTO.getEquipmentCode() != null, ErpMps::getEquipmentCode, queryDTO.getEquipmentCode())
               .like(queryDTO.getEquipmentName() != null, ErpMps::getEquipmentName, queryDTO.getEquipmentName())
               .eq(queryDTO.getPriority() != null, ErpMps::getPriority, queryDTO.getPriority())
               .eq(queryDTO.getStatus() != null, ErpMps::getStatus, queryDTO.getStatus())
               .orderByDesc(ErpMps::getPriority)
               .orderByDesc(ErpMps::getPlanStartDate)
               .orderByDesc(ErpMps::getCreateTime);
        
        Page<ErpMps> result = mpsMapper.selectPage(page, wrapper);
        
        List<MpsVO> voList = result.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        return PageResult.of(
            result.getTotal(), 
            voList, 
            (long) result.getCurrent(), 
            (long) result.getSize()
        );
    }

    public MpsVO getMpsById(Long id) {
        ErpMps mps = mpsMapper.selectById(id);
        if (mps == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return convertToVO(mps);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createMps(MpsCreateDTO createDTO) {
        if (createDTO.getMpsNo() == null || createDTO.getMpsNo().isEmpty()) {
            String mpsNo = "MPS" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + 
                String.format("%06d", System.currentTimeMillis() % 1000000);
            createDTO.setMpsNo(mpsNo);
        }

        ErpMps mps = new ErpMps();
        mps.setMpsNo(createDTO.getMpsNo());
        mps.setPlanName(createDTO.getPlanName());
        mps.setPlanType(createDTO.getPlanType());
        mps.setProductId(createDTO.getProductId());
        mps.setProductCode(createDTO.getProductCode());
        mps.setProductName(createDTO.getProductName());
        mps.setSpecification(createDTO.getSpecification());
        mps.setUnit(createDTO.getUnit());
        mps.setNetRequirement(createDTO.getNetRequirement());
        mps.setPlannedQuantity(createDTO.getPlannedQuantity());
        mps.setPlanStartDate(createDTO.getPlanStartDate());
        mps.setPlanEndDate(createDTO.getPlanEndDate());
        mps.setEquipmentId(createDTO.getEquipmentId());
        mps.setEquipmentCode(createDTO.getEquipmentCode());
        mps.setEquipmentName(createDTO.getEquipmentName());
        mps.setGroupId(createDTO.getGroupId());
        mps.setGroupCode(createDTO.getGroupCode());
        mps.setGroupName(createDTO.getGroupName());
        mps.setPriority(createDTO.getPriority());
        mps.setRemark(createDTO.getRemark());
        mps.setStatus(createDTO.getStatus());
        
        mpsMapper.insert(mps);
        log.info("创建主生产计划成功: {}", createDTO.getMpsNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateMps(MpsUpdateDTO updateDTO) {
        ErpMps mps = mpsMapper.selectById(updateDTO.getId());
        if (mps == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        if (updateDTO.getPlanName() != null) {
            mps.setPlanName(updateDTO.getPlanName());
        }
        if (updateDTO.getPlannedQuantity() != null) {
            mps.setPlannedQuantity(updateDTO.getPlannedQuantity());
        }
        if (updateDTO.getPlanStartDate() != null) {
            mps.setPlanStartDate(updateDTO.getPlanStartDate());
        }
        if (updateDTO.getPlanEndDate() != null) {
            mps.setPlanEndDate(updateDTO.getPlanEndDate());
        }
        if (updateDTO.getActualStartDate() != null) {
            mps.setActualStartDate(updateDTO.getActualStartDate());
        }
        if (updateDTO.getActualEndDate() != null) {
            mps.setActualEndDate(updateDTO.getActualEndDate());
        }
        if (updateDTO.getEquipmentId() != null) {
            mps.setEquipmentId(updateDTO.getEquipmentId());
        }
        if (updateDTO.getEquipmentCode() != null) {
            mps.setEquipmentCode(updateDTO.getEquipmentCode());
        }
        if (updateDTO.getEquipmentName() != null) {
            mps.setEquipmentName(updateDTO.getEquipmentName());
        }
        if (updateDTO.getGroupId() != null) {
            mps.setGroupId(updateDTO.getGroupId());
        }
        if (updateDTO.getGroupCode() != null) {
            mps.setGroupCode(updateDTO.getGroupCode());
        }
        if (updateDTO.getGroupName() != null) {
            mps.setGroupName(updateDTO.getGroupName());
        }
        if (updateDTO.getPriority() != null) {
            mps.setPriority(updateDTO.getPriority());
        }
        if (updateDTO.getRemark() != null) {
            mps.setRemark(updateDTO.getRemark());
        }
        if (updateDTO.getStatus() != null) {
            mps.setStatus(updateDTO.getStatus());
        }
        
        mpsMapper.updateById(mps);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteMps(Long id) {
        ErpMps mps = mpsMapper.selectById(id);
        if (mps == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        mpsMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateMpsStatus(Long id, Integer status) {
        ErpMps mps = mpsMapper.selectById(id);
        if (mps == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        mps.setStatus(status);
        mpsMapper.updateById(mps);
    }

    @Transactional(rollbackFor = Exception.class)
    public void generateMpsFromNetRequirement() {
        log.info("开始从净需求生成主生产计划...");
        
        List<ErpNetRequirement> netRequirements = netRequirementMapper.selectList(
            new LambdaQueryWrapper<ErpNetRequirement>()
                .eq(ErpNetRequirement::getStatus, 1)
                .orderByAsc(ErpNetRequirement::getRequirementDate)
        );
        
        for (ErpNetRequirement netReq : netRequirements) {
            if (netReq.getNetRequirement() == null || netReq.getNetRequirement().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            
            Long count = mpsMapper.selectCount(
                new LambdaQueryWrapper<ErpMps>()
                    .eq(ErpMps::getProductId, netReq.getProductId())
                    .eq(ErpMps::getStatus, 1)
            );
            
            if (count > 0) {
                log.info("产品 {} 已有未完成的主生产计划，跳过", netReq.getProductCode());
                continue;
            }
            
            BalanceResult balanceResult = calculateCapacityBalance(netReq);
            
            ErpMps mps = new ErpMps();
            String mpsNo = "MPS" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + 
                String.format("%06d", System.currentTimeMillis() % 1000000);
            mps.setMpsNo(mpsNo);
            mps.setPlanName("自动生成计划-" + netReq.getProductName());
            mps.setPlanType(1);
            mps.setProductId(netReq.getProductId());
            mps.setProductCode(netReq.getProductCode());
            mps.setProductName(netReq.getProductName());
            mps.setNetRequirement(netReq.getNetRequirement());
            mps.setPlannedQuantity(netReq.getNetRequirement());
            mps.setPlanStartDate(netReq.getRequirementDate());
            mps.setPlanEndDate(balanceResult.planEndDate);
            mps.setEquipmentId(balanceResult.equipmentId);
            mps.setEquipmentCode(balanceResult.equipmentCode);
            mps.setEquipmentName(balanceResult.equipmentName);
            mps.setGroupId(balanceResult.groupId);
            mps.setGroupCode(balanceResult.groupCode);
            mps.setGroupName(balanceResult.groupName);
            mps.setPriority(2);
            mps.setCapacityRequired(balanceResult.capacityRequired);
            mps.setCapacityAvailable(balanceResult.capacityAvailable);
            mps.setCapacityUtilization(balanceResult.capacityUtilization);
            mps.setRemark("从净需求自动生成，需求数量: " + netReq.getNetRequirement());
            mps.setStatus(1);
            
            mpsMapper.insert(mps);
            log.info("生成主生产计划: {} - {} - {}", mpsNo, netReq.getProductName(), netReq.getNetRequirement());
        }
        
        log.info("主生产计划生成完成");
    }

    private BalanceResult calculateCapacityBalance(ErpNetRequirement netReq) {
        BalanceResult result = new BalanceResult();
        
        List<ErpEquipment> equipments = equipmentMapper.selectList(
            new LambdaQueryWrapper<ErpEquipment>()
                .eq(ErpEquipment::getStatus, 1)
                .orderByAsc(ErpEquipment::getId)
        );
        
        if (equipments.isEmpty()) {
            result.planEndDate = netReq.getRequirementDate().plusDays(7);
            result.capacityRequired = netReq.getNetRequirement();
            result.capacityAvailable = BigDecimal.ZERO;
            result.capacityUtilization = BigDecimal.ZERO;
            return result;
        }
        
        ErpEquipment equipment = equipments.get(0);
        result.equipmentId = equipment.getId();
        result.equipmentCode = equipment.getEquipmentCode();
        result.equipmentName = equipment.getEquipmentName();
        
        List<ErpWorkGroup> workGroups = workGroupMapper.selectList(
            new LambdaQueryWrapper<ErpWorkGroup>()
                .eq(ErpWorkGroup::getStatus, 1)
                .orderByAsc(ErpWorkGroup::getId)
        );
        
        if (!workGroups.isEmpty()) {
            ErpWorkGroup workGroup = workGroups.get(0);
            result.groupId = workGroup.getId();
            result.groupCode = workGroup.getGroupCode();
            result.groupName = workGroup.getGroupName();
        }
        
        BigDecimal capacityPerDay = BigDecimal.ZERO;
        if (equipment.getCapacityPerHour() != null) {
            capacityPerDay = equipment.getCapacityPerHour().multiply(new BigDecimal(8));
        }
        
        if (capacityPerDay.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal days = netReq.getNetRequirement().divide(capacityPerDay, 0, RoundingMode.UP);
            result.planEndDate = netReq.getRequirementDate().plusDays(days.longValue());
            result.capacityRequired = netReq.getNetRequirement();
            result.capacityAvailable = capacityPerDay.multiply(new BigDecimal(days.longValue()));
            result.capacityUtilization = netReq.getNetRequirement()
                .divide(result.capacityAvailable, 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100));
        } else {
            result.planEndDate = netReq.getRequirementDate().plusDays(7);
            result.capacityRequired = netReq.getNetRequirement();
            result.capacityAvailable = BigDecimal.ZERO;
            result.capacityUtilization = BigDecimal.ZERO;
        }
        
        return result;
    }

    private MpsVO convertToVO(ErpMps mps) {
        String planTypeName = switch (mps.getPlanType()) {
            case 1 -> "正式计划";
            case 2 -> "模拟计划";
            default -> "未知";
        };
        
        String priorityName = switch (mps.getPriority()) {
            case 1 -> "高";
            case 2 -> "中";
            case 3 -> "低";
            default -> "未知";
        };
        
        String statusName = switch (mps.getStatus()) {
            case 1 -> "草稿";
            case 2 -> "已确认";
            case 3 -> "生产中";
            case 4 -> "已完成";
            case 5 -> "已取消";
            default -> "未知";
        };
        
        return MpsVO.builder()
            .id(mps.getId())
            .mpsNo(mps.getMpsNo())
            .planName(mps.getPlanName())
            .planType(mps.getPlanType())
            .planTypeName(planTypeName)
            .productId(mps.getProductId())
            .productCode(mps.getProductCode())
            .productName(mps.getProductName())
            .specification(mps.getSpecification())
            .unit(mps.getUnit())
            .netRequirement(mps.getNetRequirement())
            .plannedQuantity(mps.getPlannedQuantity())
            .planStartDate(mps.getPlanStartDate())
            .planEndDate(mps.getPlanEndDate())
            .actualStartDate(mps.getActualStartDate())
            .actualEndDate(mps.getActualEndDate())
            .equipmentId(mps.getEquipmentId())
            .equipmentCode(mps.getEquipmentCode())
            .equipmentName(mps.getEquipmentName())
            .groupId(mps.getGroupId())
            .groupCode(mps.getGroupCode())
            .groupName(mps.getGroupName())
            .priority(mps.getPriority())
            .priorityName(priorityName)
            .capacityRequired(mps.getCapacityRequired())
            .capacityAvailable(mps.getCapacityAvailable())
            .capacityUtilization(mps.getCapacityUtilization())
            .remark(mps.getRemark())
            .status(mps.getStatus())
            .statusName(statusName)
            .createBy(mps.getCreateBy())
            .createTime(mps.getCreateTime())
            .updateBy(mps.getUpdateBy())
            .updateTime(mps.getUpdateTime())
            .build();
    }

    @lombok.Data
    private static class BalanceResult {
        private LocalDate planEndDate;
        private Long equipmentId;
        private String equipmentCode;
        private String equipmentName;
        private Long groupId;
        private String groupCode;
        private String groupName;
        private BigDecimal capacityRequired;
        private BigDecimal capacityAvailable;
        private BigDecimal capacityUtilization;
    }
}
