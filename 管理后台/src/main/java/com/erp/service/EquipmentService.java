package com.erp.service;

import com.baserbac.common.enums.ResultCode;
import com.baserbac.common.exception.BusinessException;
import com.baserbac.common.result.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.dto.EquipmentCreateDTO;
import com.erp.dto.EquipmentQueryDTO;
import com.erp.dto.EquipmentUpdateDTO;
import com.erp.entity.ErpEquipment;
import com.erp.mapper.EquipmentMapper;
import com.erp.vo.EquipmentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentMapper equipmentMapper;

    public PageResult<EquipmentVO> pageEquipments(EquipmentQueryDTO queryDTO) {
        Page<ErpEquipment> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<ErpEquipment> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(queryDTO.getEquipmentCode() != null, ErpEquipment::getEquipmentCode, queryDTO.getEquipmentCode())
               .like(queryDTO.getEquipmentName() != null, ErpEquipment::getEquipmentName, queryDTO.getEquipmentName())
               .eq(queryDTO.getEquipmentType() != null, ErpEquipment::getEquipmentType, queryDTO.getEquipmentType())
               .eq(queryDTO.getBrand() != null, ErpEquipment::getBrand, queryDTO.getBrand())
               .eq(queryDTO.getWorkshop() != null, ErpEquipment::getWorkshop, queryDTO.getWorkshop())
               .eq(queryDTO.getWorkcenter() != null, ErpEquipment::getWorkcenter, queryDTO.getWorkcenter())
               .eq(queryDTO.getStatus() != null, ErpEquipment::getStatus, queryDTO.getStatus())
               .orderByDesc(ErpEquipment::getCreateTime);
        
        Page<ErpEquipment> result = equipmentMapper.selectPage(page, wrapper);
        
        List<EquipmentVO> voList = result.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        return PageResult.of(
            result.getTotal(), 
            voList, 
            (long) result.getCurrent(), 
            (long) result.getSize()
        );
    }

    public EquipmentVO getEquipmentById(Long id) {
        ErpEquipment equipment = equipmentMapper.selectById(id);
        if (equipment == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return convertToVO(equipment);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createEquipment(EquipmentCreateDTO createDTO) {
        Long count = equipmentMapper.selectCount(
            new LambdaQueryWrapper<ErpEquipment>()
                .eq(ErpEquipment::getEquipmentCode, createDTO.getEquipmentCode())
        );
        if (count > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "设备编码已存在");
        }

        ErpEquipment equipment = new ErpEquipment();
        equipment.setEquipmentCode(createDTO.getEquipmentCode());
        equipment.setEquipmentName(createDTO.getEquipmentName());
        equipment.setEquipmentType(createDTO.getEquipmentType());
        equipment.setSpecification(createDTO.getSpecification());
        equipment.setBrand(createDTO.getBrand());
        equipment.setModel(createDTO.getModel());
        equipment.setCapacityPerHour(createDTO.getCapacityPerHour());
        equipment.setCapacityUnit(createDTO.getCapacityUnit());
        equipment.setPurchaseDate(createDTO.getPurchaseDate());
        equipment.setWarrantyExpiryDate(createDTO.getWarrantyExpiryDate());
        equipment.setWorkshop(createDTO.getWorkshop());
        equipment.setWorkcenter(createDTO.getWorkcenter());
        equipment.setMaintenanceDate(createDTO.getMaintenanceDate());
        equipment.setMaintenanceIntervalDays(createDTO.getMaintenanceIntervalDays());
        equipment.setResponsiblePerson(createDTO.getResponsiblePerson());
        equipment.setRemark(createDTO.getRemark());
        equipment.setStatus(createDTO.getStatus());
        
        equipmentMapper.insert(equipment);
        log.info("创建设备成功: {}", createDTO.getEquipmentCode());
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateEquipment(EquipmentUpdateDTO updateDTO) {
        ErpEquipment equipment = equipmentMapper.selectById(updateDTO.getId());
        if (equipment == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        if (updateDTO.getEquipmentName() != null) {
            equipment.setEquipmentName(updateDTO.getEquipmentName());
        }
        if (updateDTO.getEquipmentType() != null) {
            equipment.setEquipmentType(updateDTO.getEquipmentType());
        }
        if (updateDTO.getSpecification() != null) {
            equipment.setSpecification(updateDTO.getSpecification());
        }
        if (updateDTO.getBrand() != null) {
            equipment.setBrand(updateDTO.getBrand());
        }
        if (updateDTO.getModel() != null) {
            equipment.setModel(updateDTO.getModel());
        }
        if (updateDTO.getCapacityPerHour() != null) {
            equipment.setCapacityPerHour(updateDTO.getCapacityPerHour());
        }
        if (updateDTO.getCapacityUnit() != null) {
            equipment.setCapacityUnit(updateDTO.getCapacityUnit());
        }
        if (updateDTO.getPurchaseDate() != null) {
            equipment.setPurchaseDate(updateDTO.getPurchaseDate());
        }
        if (updateDTO.getWarrantyExpiryDate() != null) {
            equipment.setWarrantyExpiryDate(updateDTO.getWarrantyExpiryDate());
        }
        if (updateDTO.getWorkshop() != null) {
            equipment.setWorkshop(updateDTO.getWorkshop());
        }
        if (updateDTO.getWorkcenter() != null) {
            equipment.setWorkcenter(updateDTO.getWorkcenter());
        }
        if (updateDTO.getMaintenanceDate() != null) {
            equipment.setMaintenanceDate(updateDTO.getMaintenanceDate());
        }
        if (updateDTO.getMaintenanceIntervalDays() != null) {
            equipment.setMaintenanceIntervalDays(updateDTO.getMaintenanceIntervalDays());
        }
        if (updateDTO.getResponsiblePerson() != null) {
            equipment.setResponsiblePerson(updateDTO.getResponsiblePerson());
        }
        if (updateDTO.getRemark() != null) {
            equipment.setRemark(updateDTO.getRemark());
        }
        if (updateDTO.getStatus() != null) {
            equipment.setStatus(updateDTO.getStatus());
        }
        
        equipmentMapper.updateById(equipment);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteEquipment(Long id) {
        ErpEquipment equipment = equipmentMapper.selectById(id);
        if (equipment == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        equipmentMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateEquipmentStatus(Long id, Integer status) {
        ErpEquipment equipment = equipmentMapper.selectById(id);
        if (equipment == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        equipment.setStatus(status);
        equipmentMapper.updateById(equipment);
    }

    private EquipmentVO convertToVO(ErpEquipment equipment) {
        String statusName = switch (equipment.getStatus()) {
            case 1 -> "运行中";
            case 2 -> "停机维护";
            case 3 -> "故障";
            case 0 -> "停用";
            default -> "未知";
        };
        
        return EquipmentVO.builder()
            .id(equipment.getId())
            .equipmentCode(equipment.getEquipmentCode())
            .equipmentName(equipment.getEquipmentName())
            .equipmentType(equipment.getEquipmentType())
            .specification(equipment.getSpecification())
            .brand(equipment.getBrand())
            .model(equipment.getModel())
            .capacityPerHour(equipment.getCapacityPerHour())
            .capacityUnit(equipment.getCapacityUnit())
            .purchaseDate(equipment.getPurchaseDate())
            .warrantyExpiryDate(equipment.getWarrantyExpiryDate())
            .workshop(equipment.getWorkshop())
            .workcenter(equipment.getWorkcenter())
            .maintenanceDate(equipment.getMaintenanceDate())
            .maintenanceIntervalDays(equipment.getMaintenanceIntervalDays())
            .responsiblePerson(equipment.getResponsiblePerson())
            .remark(equipment.getRemark())
            .status(equipment.getStatus())
            .statusName(statusName)
            .createBy(equipment.getCreateBy())
            .createTime(equipment.getCreateTime())
            .updateBy(equipment.getUpdateBy())
            .updateTime(equipment.getUpdateTime())
            .build();
    }
}
