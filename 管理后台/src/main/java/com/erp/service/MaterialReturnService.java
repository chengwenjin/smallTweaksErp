package com.erp.service;

import com.baserbac.common.enums.ResultCode;
import com.baserbac.common.exception.BusinessException;
import com.baserbac.common.result.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.dto.MaterialReturnCreateDTO;
import com.erp.dto.MaterialReturnQueryDTO;
import com.erp.dto.MaterialReturnUpdateDTO;
import com.erp.entity.ErpMaterialReturn;
import com.erp.mapper.MaterialReturnMapper;
import com.erp.vo.MaterialReturnVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialReturnService {

    private final MaterialReturnMapper materialReturnMapper;

    public static final int RETURN_TYPE_SURPLUS = 1;
    public static final int RETURN_TYPE_DEFECTIVE = 2;

    public static final int STATUS_NEW = 1;
    public static final int STATUS_PENDING_APPROVAL = 2;
    public static final int STATUS_APPROVED = 3;
    public static final int STATUS_COMPLETED = 4;

    private static final Map<Integer, String> RETURN_TYPE_NAMES = new HashMap<>();
    static {
        RETURN_TYPE_NAMES.put(RETURN_TYPE_SURPLUS, "余料退回");
        RETURN_TYPE_NAMES.put(RETURN_TYPE_DEFECTIVE, "不良品补料");
    }

    private static final Map<Integer, String> STATUS_NAMES = new HashMap<>();
    static {
        STATUS_NAMES.put(STATUS_NEW, "新建");
        STATUS_NAMES.put(STATUS_PENDING_APPROVAL, "待审批");
        STATUS_NAMES.put(STATUS_APPROVED, "已审批");
        STATUS_NAMES.put(STATUS_COMPLETED, "已完成");
    }

    public PageResult<MaterialReturnVO> pageMaterialReturns(MaterialReturnQueryDTO queryDTO) {
        Page<ErpMaterialReturn> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<ErpMaterialReturn> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getReturnNo()), ErpMaterialReturn::getReturnNo, queryDTO.getReturnNo())
               .eq(queryDTO.getReturnType() != null, ErpMaterialReturn::getReturnType, queryDTO.getReturnType())
               .like(StringUtils.hasText(queryDTO.getWorkOrderNo()), ErpMaterialReturn::getWorkOrderNo, queryDTO.getWorkOrderNo())
               .like(StringUtils.hasText(queryDTO.getWorkOrderName()), ErpMaterialReturn::getWorkOrderName, queryDTO.getWorkOrderName())
               .like(StringUtils.hasText(queryDTO.getPickNo()), ErpMaterialReturn::getPickNo, queryDTO.getPickNo())
               .like(StringUtils.hasText(queryDTO.getProductCode()), ErpMaterialReturn::getProductCode, queryDTO.getProductCode())
               .like(StringUtils.hasText(queryDTO.getProductName()), ErpMaterialReturn::getProductName, queryDTO.getProductName())
               .like(StringUtils.hasText(queryDTO.getMaterialCode()), ErpMaterialReturn::getMaterialCode, queryDTO.getMaterialCode())
               .like(StringUtils.hasText(queryDTO.getMaterialName()), ErpMaterialReturn::getMaterialName, queryDTO.getMaterialName())
               .like(StringUtils.hasText(queryDTO.getOperatorName()), ErpMaterialReturn::getOperatorName, queryDTO.getOperatorName())
               .like(StringUtils.hasText(queryDTO.getBatchNo()), ErpMaterialReturn::getBatchNo, queryDTO.getBatchNo())
               .like(StringUtils.hasText(queryDTO.getWarehouseCode()), ErpMaterialReturn::getWarehouseCode, queryDTO.getWarehouseCode())
               .like(StringUtils.hasText(queryDTO.getWarehouseName()), ErpMaterialReturn::getWarehouseName, queryDTO.getWarehouseName())
               .eq(queryDTO.getStatus() != null, ErpMaterialReturn::getStatus, queryDTO.getStatus())
               .orderByDesc(ErpMaterialReturn::getCreateTime);
        
        Page<ErpMaterialReturn> result = materialReturnMapper.selectPage(page, wrapper);
        
        List<MaterialReturnVO> voList = result.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        return PageResult.of(
            result.getTotal(), 
            voList, 
            (long) result.getCurrent(), 
            (long) result.getSize()
        );
    }

    public MaterialReturnVO getMaterialReturnById(Long id) {
        ErpMaterialReturn returnOrder = materialReturnMapper.selectById(id);
        if (returnOrder == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return convertToVO(returnOrder);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createMaterialReturn(MaterialReturnCreateDTO createDTO) {
        ErpMaterialReturn returnOrder = new ErpMaterialReturn();
        
        String returnNo = generateReturnNo();
        returnOrder.setReturnNo(returnNo);
        returnOrder.setReturnType(createDTO.getReturnType() != null ? createDTO.getReturnType() : RETURN_TYPE_SURPLUS);
        returnOrder.setWorkOrderId(createDTO.getWorkOrderId());
        returnOrder.setWorkOrderNo(createDTO.getWorkOrderNo());
        returnOrder.setWorkOrderName(createDTO.getWorkOrderName());
        returnOrder.setPickId(createDTO.getPickId());
        returnOrder.setPickNo(createDTO.getPickNo());
        returnOrder.setProductId(createDTO.getProductId());
        returnOrder.setProductCode(createDTO.getProductCode());
        returnOrder.setProductName(createDTO.getProductName());
        returnOrder.setMaterialId(createDTO.getMaterialId());
        returnOrder.setMaterialCode(createDTO.getMaterialCode());
        returnOrder.setMaterialName(createDTO.getMaterialName());
        returnOrder.setSpecification(createDTO.getSpecification());
        returnOrder.setUnit(createDTO.getUnit());
        returnOrder.setReturnQuantity(createDTO.getReturnQuantity());
        returnOrder.setReturnValue(createDTO.getReturnValue());
        returnOrder.setReturnReason(createDTO.getReturnReason());
        returnOrder.setReturnDate(createDTO.getReturnDate());
        returnOrder.setWarehouseId(createDTO.getWarehouseId());
        returnOrder.setWarehouseCode(createDTO.getWarehouseCode());
        returnOrder.setWarehouseName(createDTO.getWarehouseName());
        returnOrder.setOperatorId(createDTO.getOperatorId());
        returnOrder.setOperatorName(createDTO.getOperatorName());
        returnOrder.setBatchNo(createDTO.getBatchNo());
        returnOrder.setRemark(createDTO.getRemark());
        returnOrder.setStatus(STATUS_NEW);
        
        materialReturnMapper.insert(returnOrder);
        
        log.info("创建退补料单成功: {}", returnNo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateMaterialReturn(MaterialReturnUpdateDTO updateDTO) {
        ErpMaterialReturn returnOrder = materialReturnMapper.selectById(updateDTO.getId());
        if (returnOrder == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        if (updateDTO.getReturnType() != null) {
            returnOrder.setReturnType(updateDTO.getReturnType());
        }
        if (updateDTO.getReturnQuantity() != null) {
            returnOrder.setReturnQuantity(updateDTO.getReturnQuantity());
        }
        if (updateDTO.getReturnValue() != null) {
            returnOrder.setReturnValue(updateDTO.getReturnValue());
        }
        if (updateDTO.getReturnReason() != null) {
            returnOrder.setReturnReason(updateDTO.getReturnReason());
        }
        if (updateDTO.getReturnDate() != null) {
            returnOrder.setReturnDate(updateDTO.getReturnDate());
        }
        if (updateDTO.getWarehouseId() != null) {
            returnOrder.setWarehouseId(updateDTO.getWarehouseId());
        }
        if (updateDTO.getWarehouseCode() != null) {
            returnOrder.setWarehouseCode(updateDTO.getWarehouseCode());
        }
        if (updateDTO.getWarehouseName() != null) {
            returnOrder.setWarehouseName(updateDTO.getWarehouseName());
        }
        if (updateDTO.getOperatorId() != null) {
            returnOrder.setOperatorId(updateDTO.getOperatorId());
        }
        if (updateDTO.getOperatorName() != null) {
            returnOrder.setOperatorName(updateDTO.getOperatorName());
        }
        if (updateDTO.getStatus() != null) {
            returnOrder.setStatus(updateDTO.getStatus());
        }
        if (updateDTO.getBatchNo() != null) {
            returnOrder.setBatchNo(updateDTO.getBatchNo());
        }
        if (updateDTO.getRemark() != null) {
            returnOrder.setRemark(updateDTO.getRemark());
        }
        
        materialReturnMapper.updateById(returnOrder);
        
        log.info("更新退补料单成功: {}", returnOrder.getReturnNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void submitApproval(Long id) {
        ErpMaterialReturn returnOrder = materialReturnMapper.selectById(id);
        if (returnOrder == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (returnOrder.getStatus() != STATUS_NEW) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "只有新建状态的退补料单才能提交审批");
        }
        
        returnOrder.setStatus(STATUS_PENDING_APPROVAL);
        materialReturnMapper.updateById(returnOrder);
        
        log.info("退补料单提交审批成功: {}", returnOrder.getReturnNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void approval(Long id, Boolean approved, String opinion) {
        ErpMaterialReturn returnOrder = materialReturnMapper.selectById(id);
        if (returnOrder == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (returnOrder.getStatus() != STATUS_PENDING_APPROVAL) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "只有待审批状态的退补料单才能审批");
        }
        
        returnOrder.setApprovalUserId(1L);
        returnOrder.setApprovalUserName("管理员");
        returnOrder.setApprovalTime(LocalDateTime.now());
        returnOrder.setApprovalOpinion(opinion);
        
        if (Boolean.TRUE.equals(approved)) {
            returnOrder.setStatus(STATUS_APPROVED);
        } else {
            returnOrder.setStatus(STATUS_NEW);
        }
        
        materialReturnMapper.updateById(returnOrder);
        
        log.info("退补料单审批成功: {}, 结果={}", returnOrder.getReturnNo(), approved ? "通过" : "驳回");
    }

    @Transactional(rollbackFor = Exception.class)
    public void complete(Long id) {
        ErpMaterialReturn returnOrder = materialReturnMapper.selectById(id);
        if (returnOrder == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (returnOrder.getStatus() != STATUS_APPROVED) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "只有已审批状态的退补料单才能完成");
        }
        
        returnOrder.setStatus(STATUS_COMPLETED);
        materialReturnMapper.updateById(returnOrder);
        
        log.info("退补料单完成: {}", returnOrder.getReturnNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteMaterialReturn(Long id) {
        ErpMaterialReturn returnOrder = materialReturnMapper.selectById(id);
        if (returnOrder == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (returnOrder.getStatus() == STATUS_COMPLETED) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "已完成的退补料单不能删除");
        }
        
        materialReturnMapper.deleteById(id);
        
        log.info("删除退补料单成功: {}", returnOrder.getReturnNo());
    }

    private String generateReturnNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        Long count = materialReturnMapper.selectCount(
            new LambdaQueryWrapper<ErpMaterialReturn>()
                .likeRight(ErpMaterialReturn::getReturnNo, "RT" + dateStr)
        );
        
        long seq = (count != null ? count : 0) + 1;
        return String.format("RT%s%05d", dateStr, seq);
    }

    private MaterialReturnVO convertToVO(ErpMaterialReturn returnOrder) {
        String returnTypeName = RETURN_TYPE_NAMES.get(returnOrder.getReturnType());
        if (returnTypeName == null) {
            returnTypeName = "未知";
        }
        
        String statusName = STATUS_NAMES.get(returnOrder.getStatus());
        if (statusName == null) {
            statusName = "未知";
        }
        
        return MaterialReturnVO.builder()
            .id(returnOrder.getId())
            .returnNo(returnOrder.getReturnNo())
            .returnType(returnOrder.getReturnType())
            .returnTypeName(returnTypeName)
            .workOrderId(returnOrder.getWorkOrderId())
            .workOrderNo(returnOrder.getWorkOrderNo())
            .workOrderName(returnOrder.getWorkOrderName())
            .pickId(returnOrder.getPickId())
            .pickNo(returnOrder.getPickNo())
            .productId(returnOrder.getProductId())
            .productCode(returnOrder.getProductCode())
            .productName(returnOrder.getProductName())
            .materialId(returnOrder.getMaterialId())
            .materialCode(returnOrder.getMaterialCode())
            .materialName(returnOrder.getMaterialName())
            .specification(returnOrder.getSpecification())
            .unit(returnOrder.getUnit())
            .returnQuantity(returnOrder.getReturnQuantity())
            .returnValue(returnOrder.getReturnValue())
            .returnReason(returnOrder.getReturnReason())
            .returnDate(returnOrder.getReturnDate())
            .warehouseId(returnOrder.getWarehouseId())
            .warehouseCode(returnOrder.getWarehouseCode())
            .warehouseName(returnOrder.getWarehouseName())
            .operatorId(returnOrder.getOperatorId())
            .operatorName(returnOrder.getOperatorName())
            .batchNo(returnOrder.getBatchNo())
            .remark(returnOrder.getRemark())
            .status(returnOrder.getStatus())
            .statusName(statusName)
            .approvalUserId(returnOrder.getApprovalUserId())
            .approvalUserName(returnOrder.getApprovalUserName())
            .approvalTime(returnOrder.getApprovalTime())
            .approvalOpinion(returnOrder.getApprovalOpinion())
            .createBy(returnOrder.getCreateBy())
            .createTime(returnOrder.getCreateTime())
            .updateBy(returnOrder.getUpdateBy())
            .updateTime(returnOrder.getUpdateTime())
            .build();
    }
}
