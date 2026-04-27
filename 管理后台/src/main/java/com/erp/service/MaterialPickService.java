package com.erp.service;

import com.baserbac.common.enums.ResultCode;
import com.baserbac.common.exception.BusinessException;
import com.baserbac.common.result.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.dto.MaterialPickCreateDTO;
import com.erp.dto.MaterialPickQueryDTO;
import com.erp.dto.MaterialPickUpdateDTO;
import com.erp.dto.OverPickApprovalCreateDTO;
import com.erp.entity.ErpMaterialPick;
import com.erp.entity.ErpOverPickApproval;
import com.erp.mapper.MaterialPickMapper;
import com.erp.mapper.OverPickApprovalMapper;
import com.erp.vo.MaterialPickVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialPickService {

    private final MaterialPickMapper materialPickMapper;
    private final OverPickApprovalMapper overPickApprovalMapper;

    public static final int STATUS_NEW = 1;
    public static final int STATUS_PENDING_APPROVAL = 2;
    public static final int STATUS_APPROVED = 3;
    public static final int STATUS_PICKED = 4;
    public static final int STATUS_OVER_PICK_PENDING = 5;

    private static final Map<Integer, String> STATUS_NAMES = new HashMap<>();
    static {
        STATUS_NAMES.put(STATUS_NEW, "新建");
        STATUS_NAMES.put(STATUS_PENDING_APPROVAL, "待审批");
        STATUS_NAMES.put(STATUS_APPROVED, "已审批");
        STATUS_NAMES.put(STATUS_PICKED, "已领料");
        STATUS_NAMES.put(STATUS_OVER_PICK_PENDING, "超领待审批");
    }

    public PageResult<MaterialPickVO> pageMaterialPicks(MaterialPickQueryDTO queryDTO) {
        Page<ErpMaterialPick> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<ErpMaterialPick> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getPickNo()), ErpMaterialPick::getPickNo, queryDTO.getPickNo())
               .like(StringUtils.hasText(queryDTO.getWorkOrderNo()), ErpMaterialPick::getWorkOrderNo, queryDTO.getWorkOrderNo())
               .like(StringUtils.hasText(queryDTO.getWorkOrderName()), ErpMaterialPick::getWorkOrderName, queryDTO.getWorkOrderName())
               .like(StringUtils.hasText(queryDTO.getProductCode()), ErpMaterialPick::getProductCode, queryDTO.getProductCode())
               .like(StringUtils.hasText(queryDTO.getProductName()), ErpMaterialPick::getProductName, queryDTO.getProductName())
               .like(StringUtils.hasText(queryDTO.getMaterialCode()), ErpMaterialPick::getMaterialCode, queryDTO.getMaterialCode())
               .like(StringUtils.hasText(queryDTO.getMaterialName()), ErpMaterialPick::getMaterialName, queryDTO.getMaterialName())
               .like(StringUtils.hasText(queryDTO.getPickerName()), ErpMaterialPick::getPickerName, queryDTO.getPickerName())
               .like(StringUtils.hasText(queryDTO.getBatchNo()), ErpMaterialPick::getBatchNo, queryDTO.getBatchNo())
               .like(StringUtils.hasText(queryDTO.getWarehouseCode()), ErpMaterialPick::getWarehouseCode, queryDTO.getWarehouseCode())
               .like(StringUtils.hasText(queryDTO.getWarehouseName()), ErpMaterialPick::getWarehouseName, queryDTO.getWarehouseName())
               .eq(queryDTO.getStatus() != null, ErpMaterialPick::getStatus, queryDTO.getStatus())
               .orderByDesc(ErpMaterialPick::getCreateTime);
        
        Page<ErpMaterialPick> result = materialPickMapper.selectPage(page, wrapper);
        
        List<MaterialPickVO> voList = result.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        return PageResult.of(
            result.getTotal(), 
            voList, 
            (long) result.getCurrent(), 
            (long) result.getSize()
        );
    }

    public MaterialPickVO getMaterialPickById(Long id) {
        ErpMaterialPick pick = materialPickMapper.selectById(id);
        if (pick == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return convertToVO(pick);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createMaterialPick(MaterialPickCreateDTO createDTO) {
        ErpMaterialPick pick = new ErpMaterialPick();
        
        String pickNo = generatePickNo();
        pick.setPickNo(pickNo);
        pick.setWorkOrderId(createDTO.getWorkOrderId());
        pick.setWorkOrderNo(createDTO.getWorkOrderNo());
        pick.setWorkOrderName(createDTO.getWorkOrderName());
        pick.setBomId(createDTO.getBomId());
        pick.setBomVersionId(createDTO.getBomVersionId());
        pick.setBomVersionNo(createDTO.getBomVersionNo());
        pick.setProductId(createDTO.getProductId());
        pick.setProductCode(createDTO.getProductCode());
        pick.setProductName(createDTO.getProductName());
        pick.setMaterialId(createDTO.getMaterialId());
        pick.setMaterialCode(createDTO.getMaterialCode());
        pick.setMaterialName(createDTO.getMaterialName());
        pick.setSpecification(createDTO.getSpecification());
        pick.setUnit(createDTO.getUnit());
        pick.setPlanQuantity(createDTO.getPlanQuantity() != null ? createDTO.getPlanQuantity() : BigDecimal.ZERO);
        pick.setPickedQuantity(BigDecimal.ZERO);
        pick.setRemainingQuantity(createDTO.getPlanQuantity() != null ? createDTO.getPlanQuantity() : BigDecimal.ZERO);
        pick.setPlanPickDate(createDTO.getPlanPickDate());
        pick.setWarehouseId(createDTO.getWarehouseId());
        pick.setWarehouseCode(createDTO.getWarehouseCode());
        pick.setWarehouseName(createDTO.getWarehouseName());
        pick.setPickerId(createDTO.getPickerId());
        pick.setPickerName(createDTO.getPickerName());
        pick.setBatchNo(createDTO.getBatchNo());
        pick.setRemark(createDTO.getRemark());
        pick.setStatus(STATUS_NEW);
        
        materialPickMapper.insert(pick);
        
        log.info("创建领料单成功: {}", pickNo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateMaterialPick(MaterialPickUpdateDTO updateDTO) {
        ErpMaterialPick pick = materialPickMapper.selectById(updateDTO.getId());
        if (pick == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        if (updateDTO.getPickedQuantity() != null) {
            BigDecimal planQty = pick.getPlanQuantity() != null ? pick.getPlanQuantity() : BigDecimal.ZERO;
            BigDecimal pickedQty = updateDTO.getPickedQuantity();
            
            if (pickedQty.compareTo(planQty) > 0) {
                BigDecimal overPickQty = pickedQty.subtract(planQty);
                createOverPickApproval(pick, overPickQty);
                pick.setStatus(STATUS_OVER_PICK_PENDING);
            }
            
            pick.setPickedQuantity(pickedQty);
            pick.setRemainingQuantity(planQty.subtract(pickedQty).max(BigDecimal.ZERO));
        }
        if (updateDTO.getActualPickDate() != null) {
            pick.setActualPickDate(updateDTO.getActualPickDate());
        }
        if (updateDTO.getWarehouseId() != null) {
            pick.setWarehouseId(updateDTO.getWarehouseId());
        }
        if (updateDTO.getWarehouseCode() != null) {
            pick.setWarehouseCode(updateDTO.getWarehouseCode());
        }
        if (updateDTO.getWarehouseName() != null) {
            pick.setWarehouseName(updateDTO.getWarehouseName());
        }
        if (updateDTO.getPickerId() != null) {
            pick.setPickerId(updateDTO.getPickerId());
        }
        if (updateDTO.getPickerName() != null) {
            pick.setPickerName(updateDTO.getPickerName());
        }
        if (updateDTO.getStatus() != null) {
            pick.setStatus(updateDTO.getStatus());
        }
        if (updateDTO.getBatchNo() != null) {
            pick.setBatchNo(updateDTO.getBatchNo());
        }
        if (updateDTO.getRemark() != null) {
            pick.setRemark(updateDTO.getRemark());
        }
        
        materialPickMapper.updateById(pick);
        
        log.info("更新领料单成功: {}", pick.getPickNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void pickMaterial(Long id, BigDecimal quantity) {
        ErpMaterialPick pick = materialPickMapper.selectById(id);
        if (pick == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        BigDecimal planQty = pick.getPlanQuantity() != null ? pick.getPlanQuantity() : BigDecimal.ZERO;
        BigDecimal currentPickedQty = pick.getPickedQuantity() != null ? pick.getPickedQuantity() : BigDecimal.ZERO;
        BigDecimal totalPickedQty = currentPickedQty.add(quantity);
        
        if (totalPickedQty.compareTo(planQty) > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "领料数量超出BOM定额，需要走超领审批流程");
        }
        
        pick.setPickedQuantity(totalPickedQty);
        pick.setRemainingQuantity(planQty.subtract(totalPickedQty));
        pick.setActualPickDate(LocalDate.now());
        pick.setStatus(STATUS_PICKED);
        
        materialPickMapper.updateById(pick);
        
        log.info("领料成功: 领料单={}, 领料数量={}", pick.getPickNo(), quantity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void submitApproval(Long id) {
        ErpMaterialPick pick = materialPickMapper.selectById(id);
        if (pick == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (pick.getStatus() != STATUS_NEW) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "只有新建状态的领料单才能提交审批");
        }
        
        pick.setStatus(STATUS_PENDING_APPROVAL);
        materialPickMapper.updateById(pick);
        
        log.info("领料单提交审批成功: {}", pick.getPickNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void approval(Long id, Boolean approved, String opinion) {
        ErpMaterialPick pick = materialPickMapper.selectById(id);
        if (pick == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (pick.getStatus() != STATUS_PENDING_APPROVAL) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "只有待审批状态的领料单才能审批");
        }
        
        if (Boolean.TRUE.equals(approved)) {
            pick.setStatus(STATUS_APPROVED);
        } else {
            pick.setStatus(STATUS_NEW);
        }
        
        materialPickMapper.updateById(pick);
        
        log.info("领料单审批成功: {}, 结果={}", pick.getPickNo(), approved ? "通过" : "驳回");
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteMaterialPick(Long id) {
        ErpMaterialPick pick = materialPickMapper.selectById(id);
        if (pick == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (pick.getStatus() == STATUS_PICKED) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "已领料的领料单不能删除");
        }
        
        materialPickMapper.deleteById(id);
        
        log.info("删除领料单成功: {}", pick.getPickNo());
    }

    private void createOverPickApproval(ErpMaterialPick pick, BigDecimal overPickQty) {
        ErpOverPickApproval approval = new ErpOverPickApproval();
        
        String approvalNo = "OPA" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        approval.setApprovalNo(approvalNo);
        approval.setWorkOrderId(pick.getWorkOrderId());
        approval.setWorkOrderNo(pick.getWorkOrderNo());
        approval.setWorkOrderName(pick.getWorkOrderName());
        approval.setPickId(pick.getId());
        approval.setPickNo(pick.getPickNo());
        approval.setProductId(pick.getProductId());
        approval.setProductCode(pick.getProductCode());
        approval.setProductName(pick.getProductName());
        approval.setMaterialId(pick.getMaterialId());
        approval.setMaterialCode(pick.getMaterialCode());
        approval.setMaterialName(pick.getMaterialName());
        approval.setSpecification(pick.getSpecification());
        approval.setUnit(pick.getUnit());
        approval.setPlanQuantity(pick.getPlanQuantity());
        approval.setOverPickQuantity(overPickQty);
        approval.setTotalQuantity(pick.getPlanQuantity().add(overPickQty));
        approval.setOverPickReason("领料数量超出BOM定额");
        approval.setApplicantName("系统");
        approval.setApplicationTime(LocalDateTime.now());
        approval.setStatus(OverPickApprovalService.STATUS_PENDING_APPROVAL);
        
        overPickApprovalMapper.insert(approval);
        
        log.info("创建超领审批单: {}", approvalNo);
    }

    private String generatePickNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        Long count = materialPickMapper.selectCount(
            new LambdaQueryWrapper<ErpMaterialPick>()
                .likeRight(ErpMaterialPick::getPickNo, "PK" + dateStr)
        );
        
        long seq = (count != null ? count : 0) + 1;
        return String.format("PK%s%05d", dateStr, seq);
    }

    private MaterialPickVO convertToVO(ErpMaterialPick pick) {
        String statusName = STATUS_NAMES.get(pick.getStatus());
        if (statusName == null) {
            statusName = "未知";
        }
        
        BigDecimal planQty = pick.getPlanQuantity() != null ? pick.getPlanQuantity() : BigDecimal.ZERO;
        BigDecimal pickedQty = pick.getPickedQuantity() != null ? pick.getPickedQuantity() : BigDecimal.ZERO;
        boolean isOverPick = pickedQty.compareTo(planQty) > 0;
        BigDecimal overPickQty = isOverPick ? pickedQty.subtract(planQty) : BigDecimal.ZERO;
        
        return MaterialPickVO.builder()
            .id(pick.getId())
            .pickNo(pick.getPickNo())
            .workOrderId(pick.getWorkOrderId())
            .workOrderNo(pick.getWorkOrderNo())
            .workOrderName(pick.getWorkOrderName())
            .bomId(pick.getBomId())
            .bomVersionId(pick.getBomVersionId())
            .bomVersionNo(pick.getBomVersionNo())
            .productId(pick.getProductId())
            .productCode(pick.getProductCode())
            .productName(pick.getProductName())
            .materialId(pick.getMaterialId())
            .materialCode(pick.getMaterialCode())
            .materialName(pick.getMaterialName())
            .specification(pick.getSpecification())
            .unit(pick.getUnit())
            .planQuantity(pick.getPlanQuantity())
            .pickedQuantity(pick.getPickedQuantity())
            .remainingQuantity(pick.getRemainingQuantity())
            .planPickDate(pick.getPlanPickDate())
            .actualPickDate(pick.getActualPickDate())
            .warehouseId(pick.getWarehouseId())
            .warehouseCode(pick.getWarehouseCode())
            .warehouseName(pick.getWarehouseName())
            .pickerId(pick.getPickerId())
            .pickerName(pick.getPickerName())
            .batchNo(pick.getBatchNo())
            .remark(pick.getRemark())
            .status(pick.getStatus())
            .statusName(statusName)
            .isOverPick(isOverPick)
            .overPickQuantity(overPickQty)
            .createBy(pick.getCreateBy())
            .createTime(pick.getCreateTime())
            .updateBy(pick.getUpdateBy())
            .updateTime(pick.getUpdateTime())
            .build();
    }
}
