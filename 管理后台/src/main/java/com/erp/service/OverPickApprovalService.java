package com.erp.service;

import com.baserbac.common.enums.ResultCode;
import com.baserbac.common.exception.BusinessException;
import com.baserbac.common.result.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.dto.OverPickApprovalCreateDTO;
import com.erp.dto.OverPickApprovalDTO;
import com.erp.dto.OverPickApprovalQueryDTO;
import com.erp.dto.OverPickApprovalUpdateDTO;
import com.erp.entity.ErpMaterialPick;
import com.erp.entity.ErpOverPickApproval;
import com.erp.mapper.MaterialPickMapper;
import com.erp.mapper.OverPickApprovalMapper;
import com.erp.vo.OverPickApprovalVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OverPickApprovalService {

    private final OverPickApprovalMapper overPickApprovalMapper;
    private final MaterialPickMapper materialPickMapper;

    public static final int STATUS_DRAFT = 1;
    public static final int STATUS_PENDING_APPROVAL = 2;
    public static final int STATUS_APPROVED = 3;
    public static final int STATUS_REJECTED = 4;
    public static final int STATUS_CANCELLED = 5;

    private static final Map<Integer, String> STATUS_NAMES = new HashMap<>();
    static {
        STATUS_NAMES.put(STATUS_DRAFT, "草稿");
        STATUS_NAMES.put(STATUS_PENDING_APPROVAL, "待审批");
        STATUS_NAMES.put(STATUS_APPROVED, "已通过");
        STATUS_NAMES.put(STATUS_REJECTED, "已驳回");
        STATUS_NAMES.put(STATUS_CANCELLED, "已撤销");
    }

    public PageResult<OverPickApprovalVO> pageOverPickApprovals(OverPickApprovalQueryDTO queryDTO) {
        Page<ErpOverPickApproval> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<ErpOverPickApproval> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getApprovalNo()), ErpOverPickApproval::getApprovalNo, queryDTO.getApprovalNo())
               .like(StringUtils.hasText(queryDTO.getWorkOrderNo()), ErpOverPickApproval::getWorkOrderNo, queryDTO.getWorkOrderNo())
               .like(StringUtils.hasText(queryDTO.getWorkOrderName()), ErpOverPickApproval::getWorkOrderName, queryDTO.getWorkOrderName())
               .like(StringUtils.hasText(queryDTO.getPickNo()), ErpOverPickApproval::getPickNo, queryDTO.getPickNo())
               .like(StringUtils.hasText(queryDTO.getProductCode()), ErpOverPickApproval::getProductCode, queryDTO.getProductCode())
               .like(StringUtils.hasText(queryDTO.getProductName()), ErpOverPickApproval::getProductName, queryDTO.getProductName())
               .like(StringUtils.hasText(queryDTO.getMaterialCode()), ErpOverPickApproval::getMaterialCode, queryDTO.getMaterialCode())
               .like(StringUtils.hasText(queryDTO.getMaterialName()), ErpOverPickApproval::getMaterialName, queryDTO.getMaterialName())
               .like(StringUtils.hasText(queryDTO.getApplicantName()), ErpOverPickApproval::getApplicantName, queryDTO.getApplicantName())
               .like(StringUtils.hasText(queryDTO.getApprovalUserName()), ErpOverPickApproval::getApprovalUserName, queryDTO.getApprovalUserName())
               .eq(queryDTO.getStatus() != null, ErpOverPickApproval::getStatus, queryDTO.getStatus())
               .orderByDesc(ErpOverPickApproval::getCreateTime);
        
        Page<ErpOverPickApproval> result = overPickApprovalMapper.selectPage(page, wrapper);
        
        List<OverPickApprovalVO> voList = result.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        return PageResult.of(
            result.getTotal(), 
            voList, 
            (long) result.getCurrent(), 
            (long) result.getSize()
        );
    }

    public OverPickApprovalVO getOverPickApprovalById(Long id) {
        ErpOverPickApproval approval = overPickApprovalMapper.selectById(id);
        if (approval == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return convertToVO(approval);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createOverPickApproval(OverPickApprovalCreateDTO createDTO) {
        ErpOverPickApproval approval = new ErpOverPickApproval();
        
        String approvalNo = generateApprovalNo();
        approval.setApprovalNo(approvalNo);
        approval.setWorkOrderId(createDTO.getWorkOrderId());
        approval.setWorkOrderNo(createDTO.getWorkOrderNo());
        approval.setWorkOrderName(createDTO.getWorkOrderName());
        approval.setPickId(createDTO.getPickId());
        approval.setPickNo(createDTO.getPickNo());
        approval.setProductId(createDTO.getProductId());
        approval.setProductCode(createDTO.getProductCode());
        approval.setProductName(createDTO.getProductName());
        approval.setMaterialId(createDTO.getMaterialId());
        approval.setMaterialCode(createDTO.getMaterialCode());
        approval.setMaterialName(createDTO.getMaterialName());
        approval.setSpecification(createDTO.getSpecification());
        approval.setUnit(createDTO.getUnit());
        approval.setPlanQuantity(createDTO.getPlanQuantity());
        approval.setOverPickQuantity(createDTO.getOverPickQuantity());
        approval.setTotalQuantity(createDTO.getPlanQuantity() != null && createDTO.getOverPickQuantity() != null 
            ? createDTO.getPlanQuantity().add(createDTO.getOverPickQuantity()) : null);
        approval.setOverPickReason(createDTO.getOverPickReason());
        approval.setApplicantId(createDTO.getApplicantId());
        approval.setApplicantName(createDTO.getApplicantName());
        approval.setApplicationTime(LocalDateTime.now());
        approval.setRemark(createDTO.getRemark());
        approval.setStatus(STATUS_DRAFT);
        
        overPickApprovalMapper.insert(approval);
        
        log.info("创建超领审批单成功: {}", approvalNo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateOverPickApproval(OverPickApprovalUpdateDTO updateDTO) {
        ErpOverPickApproval approval = overPickApprovalMapper.selectById(updateDTO.getId());
        if (approval == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        if (approval.getStatus() != STATUS_DRAFT) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "只有草稿状态的审批单才能修改");
        }

        if (updateDTO.getOverPickQuantity() != null) {
            approval.setOverPickQuantity(updateDTO.getOverPickQuantity());
            if (approval.getPlanQuantity() != null) {
                approval.setTotalQuantity(approval.getPlanQuantity().add(updateDTO.getOverPickQuantity()));
            }
        }
        if (updateDTO.getOverPickReason() != null) {
            approval.setOverPickReason(updateDTO.getOverPickReason());
        }
        if (updateDTO.getStatus() != null) {
            approval.setStatus(updateDTO.getStatus());
        }
        if (updateDTO.getRemark() != null) {
            approval.setRemark(updateDTO.getRemark());
        }
        
        overPickApprovalMapper.updateById(approval);
        
        log.info("更新超领审批单成功: {}", approval.getApprovalNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void submitApproval(Long id) {
        ErpOverPickApproval approval = overPickApprovalMapper.selectById(id);
        if (approval == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (approval.getStatus() != STATUS_DRAFT) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "只有草稿状态的审批单才能提交审批");
        }
        
        approval.setStatus(STATUS_PENDING_APPROVAL);
        approval.setApplicationTime(LocalDateTime.now());
        overPickApprovalMapper.updateById(approval);
        
        log.info("超领审批单提交审批成功: {}", approval.getApprovalNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void approval(OverPickApprovalDTO approvalDTO) {
        List<Long> ids = approvalDTO.getIds();
        if (CollectionUtils.isEmpty(ids) && approvalDTO.getId() != null) {
            ids = List.of(approvalDTO.getId());
        }
        
        if (CollectionUtils.isEmpty(ids)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "请选择审批单");
        }
        
        for (Long id : ids) {
            ErpOverPickApproval approval = overPickApprovalMapper.selectById(id);
            if (approval == null) {
                continue;
            }
            
            if (approval.getStatus() != STATUS_PENDING_APPROVAL) {
                continue;
            }
            
            approval.setApprovalUserId(1L);
            approval.setApprovalUserName("管理员");
            approval.setApprovalTime(LocalDateTime.now());
            approval.setApprovalOpinion(approvalDTO.getApprovalOpinion());
            
            if (Boolean.TRUE.equals(approvalDTO.getApproved())) {
                approval.setStatus(STATUS_APPROVED);
                approvePick(approval);
            } else {
                approval.setStatus(STATUS_REJECTED);
            }
            
            overPickApprovalMapper.updateById(approval);
            
            log.info("超领审批单审批成功: {}, 结果={}", approval.getApprovalNo(), approvalDTO.getApproved() ? "通过" : "驳回");
        }
    }

    private void approvePick(ErpOverPickApproval approval) {
        if (approval.getPickId() == null) {
            return;
        }
        
        ErpMaterialPick pick = materialPickMapper.selectById(approval.getPickId());
        if (pick != null) {
            pick.setStatus(MaterialPickService.STATUS_APPROVED);
            materialPickMapper.updateById(pick);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long id) {
        ErpOverPickApproval approval = overPickApprovalMapper.selectById(id);
        if (approval == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (approval.getStatus() == STATUS_APPROVED) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "已通过的审批单不能撤销");
        }
        
        approval.setStatus(STATUS_CANCELLED);
        overPickApprovalMapper.updateById(approval);
        
        log.info("撤销超领审批单成功: {}", approval.getApprovalNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteOverPickApproval(Long id) {
        ErpOverPickApproval approval = overPickApprovalMapper.selectById(id);
        if (approval == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (approval.getStatus() == STATUS_APPROVED) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "已通过的审批单不能删除");
        }
        
        overPickApprovalMapper.deleteById(id);
        
        log.info("删除超领审批单成功: {}", approval.getApprovalNo());
    }

    private String generateApprovalNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        Long count = overPickApprovalMapper.selectCount(
            new LambdaQueryWrapper<ErpOverPickApproval>()
                .likeRight(ErpOverPickApproval::getApprovalNo, "OPA" + dateStr)
        );
        
        long seq = (count != null ? count : 0) + 1;
        return String.format("OPA%s%05d", dateStr, seq);
    }

    private OverPickApprovalVO convertToVO(ErpOverPickApproval approval) {
        String statusName = STATUS_NAMES.get(approval.getStatus());
        if (statusName == null) {
            statusName = "未知";
        }
        
        return OverPickApprovalVO.builder()
            .id(approval.getId())
            .approvalNo(approval.getApprovalNo())
            .workOrderId(approval.getWorkOrderId())
            .workOrderNo(approval.getWorkOrderNo())
            .workOrderName(approval.getWorkOrderName())
            .pickId(approval.getPickId())
            .pickNo(approval.getPickNo())
            .productId(approval.getProductId())
            .productCode(approval.getProductCode())
            .productName(approval.getProductName())
            .materialId(approval.getMaterialId())
            .materialCode(approval.getMaterialCode())
            .materialName(approval.getMaterialName())
            .specification(approval.getSpecification())
            .unit(approval.getUnit())
            .planQuantity(approval.getPlanQuantity())
            .overPickQuantity(approval.getOverPickQuantity())
            .totalQuantity(approval.getTotalQuantity())
            .overPickReason(approval.getOverPickReason())
            .applicantId(approval.getApplicantId())
            .applicantName(approval.getApplicantName())
            .applicationTime(approval.getApplicationTime())
            .approvalUserId(approval.getApprovalUserId())
            .approvalUserName(approval.getApprovalUserName())
            .approvalTime(approval.getApprovalTime())
            .approvalOpinion(approval.getApprovalOpinion())
            .remark(approval.getRemark())
            .status(approval.getStatus())
            .statusName(statusName)
            .createBy(approval.getCreateBy())
            .createTime(approval.getCreateTime())
            .updateBy(approval.getUpdateBy())
            .updateTime(approval.getUpdateTime())
            .build();
    }
}
