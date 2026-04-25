package com.erp.service;

import com.baserbac.common.enums.ResultCode;
import com.baserbac.common.exception.BusinessException;
import com.baserbac.common.result.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.dto.WorkOrderApprovalDTO;
import com.erp.dto.WorkOrderCompleteDTO;
import com.erp.dto.WorkOrderCreateDTO;
import com.erp.dto.WorkOrderIssueDTO;
import com.erp.dto.WorkOrderPickDTO;
import com.erp.dto.WorkOrderQueryDTO;
import com.erp.dto.WorkOrderReportDTO;
import com.erp.dto.WorkOrderUpdateDTO;
import com.erp.entity.ErpWorkOrder;
import com.erp.entity.ErpWorkOrderLog;
import com.erp.mapper.WorkOrderLogMapper;
import com.erp.mapper.WorkOrderMapper;
import com.erp.vo.WorkOrderDashboardVO;
import com.erp.vo.WorkOrderLogVO;
import com.erp.vo.WorkOrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkOrderService {

    private final WorkOrderMapper workOrderMapper;
    private final WorkOrderLogMapper workOrderLogMapper;

    public static final int STATUS_DRAFT = 1;
    public static final int STATUS_PENDING_APPROVAL = 2;
    public static final int STATUS_APPROVED = 3;
    public static final int STATUS_ISSUED = 4;
    public static final int STATUS_PICKING = 5;
    public static final int STATUS_IN_PRODUCTION = 6;
    public static final int STATUS_REPORTING = 7;
    public static final int STATUS_PENDING_STORAGE = 8;
    public static final int STATUS_COMPLETED = 9;
    public static final int STATUS_CANCELLED = 10;

    private static final Map<Integer, String> STATUS_NAMES = new HashMap<>();
    static {
        STATUS_NAMES.put(STATUS_DRAFT, "草稿");
        STATUS_NAMES.put(STATUS_PENDING_APPROVAL, "待审批");
        STATUS_NAMES.put(STATUS_APPROVED, "已审批");
        STATUS_NAMES.put(STATUS_ISSUED, "已下发");
        STATUS_NAMES.put(STATUS_PICKING, "领料中");
        STATUS_NAMES.put(STATUS_IN_PRODUCTION, "生产中");
        STATUS_NAMES.put(STATUS_REPORTING, "报工中");
        STATUS_NAMES.put(STATUS_PENDING_STORAGE, "待入库");
        STATUS_NAMES.put(STATUS_COMPLETED, "已完工");
        STATUS_NAMES.put(STATUS_CANCELLED, "已取消");
    }

    private static final Map<Integer, String> PRIORITY_NAMES = new HashMap<>();
    static {
        PRIORITY_NAMES.put(1, "高");
        PRIORITY_NAMES.put(2, "中");
        PRIORITY_NAMES.put(3, "低");
    }

    public PageResult<WorkOrderVO> pageWorkOrders(WorkOrderQueryDTO queryDTO) {
        Page<ErpWorkOrder> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<ErpWorkOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(queryDTO.getWorkOrderNo() != null && !queryDTO.getWorkOrderNo().isEmpty(), 
                ErpWorkOrder::getWorkOrderNo, queryDTO.getWorkOrderNo())
               .like(queryDTO.getWorkOrderName() != null && !queryDTO.getWorkOrderName().isEmpty(), 
                ErpWorkOrder::getWorkOrderName, queryDTO.getWorkOrderName())
               .eq(queryDTO.getWorkOrderType() != null && !queryDTO.getWorkOrderType().isEmpty(), 
                ErpWorkOrder::getWorkOrderType, queryDTO.getWorkOrderType())
               .like(queryDTO.getProductCode() != null && !queryDTO.getProductCode().isEmpty(), 
                ErpWorkOrder::getProductCode, queryDTO.getProductCode())
               .like(queryDTO.getProductName() != null && !queryDTO.getProductName().isEmpty(), 
                ErpWorkOrder::getProductName, queryDTO.getProductName())
               .like(queryDTO.getEquipmentCode() != null && !queryDTO.getEquipmentCode().isEmpty(), 
                ErpWorkOrder::getEquipmentCode, queryDTO.getEquipmentCode())
               .like(queryDTO.getEquipmentName() != null && !queryDTO.getEquipmentName().isEmpty(), 
                ErpWorkOrder::getEquipmentName, queryDTO.getEquipmentName())
               .like(queryDTO.getGroupCode() != null && !queryDTO.getGroupCode().isEmpty(), 
                ErpWorkOrder::getGroupCode, queryDTO.getGroupCode())
               .like(queryDTO.getGroupName() != null && !queryDTO.getGroupName().isEmpty(), 
                ErpWorkOrder::getGroupName, queryDTO.getGroupName())
               .eq(queryDTO.getPriority() != null, ErpWorkOrder::getPriority, queryDTO.getPriority())
               .eq(queryDTO.getStatus() != null, ErpWorkOrder::getStatus, queryDTO.getStatus())
               .ge(queryDTO.getPlanStartDateStart() != null, ErpWorkOrder::getPlanStartDate, queryDTO.getPlanStartDateStart())
               .le(queryDTO.getPlanStartDateEnd() != null, ErpWorkOrder::getPlanStartDate, queryDTO.getPlanStartDateEnd())
               .ge(queryDTO.getPlanEndDateStart() != null, ErpWorkOrder::getPlanEndDate, queryDTO.getPlanEndDateStart())
               .le(queryDTO.getPlanEndDateEnd() != null, ErpWorkOrder::getPlanEndDate, queryDTO.getPlanEndDateEnd())
               .ge(queryDTO.getDeliveryDateStart() != null, ErpWorkOrder::getDeliveryDate, queryDTO.getDeliveryDateStart())
               .le(queryDTO.getDeliveryDateEnd() != null, ErpWorkOrder::getDeliveryDate, queryDTO.getDeliveryDateEnd())
               .orderByDesc(ErpWorkOrder::getCreateTime);
        
        Page<ErpWorkOrder> result = workOrderMapper.selectPage(page, wrapper);
        
        List<WorkOrderVO> voList = result.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        return PageResult.of(
            result.getTotal(), 
            voList, 
            (long) result.getCurrent(), 
            (long) result.getSize()
        );
    }

    public WorkOrderVO getWorkOrderById(Long id) {
        ErpWorkOrder workOrder = workOrderMapper.selectById(id);
        if (workOrder == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return convertToVO(workOrder);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createWorkOrder(WorkOrderCreateDTO createDTO) {
        ErpWorkOrder workOrder = new ErpWorkOrder();
        
        String workOrderNo = generateWorkOrderNo();
        workOrder.setWorkOrderNo(workOrderNo);
        workOrder.setWorkOrderName(createDTO.getWorkOrderName());
        workOrder.setWorkOrderType(createDTO.getWorkOrderType() != null ? createDTO.getWorkOrderType() : "正常工单");
        
        workOrder.setSourceMpsId(createDTO.getSourceMpsId());
        workOrder.setSourceMpsNo(createDTO.getSourceMpsNo());
        
        workOrder.setProductId(createDTO.getProductId());
        workOrder.setProductCode(createDTO.getProductCode());
        workOrder.setProductName(createDTO.getProductName());
        workOrder.setSpecification(createDTO.getSpecification());
        workOrder.setUnit(createDTO.getUnit());
        
        workOrder.setPlanQuantity(createDTO.getPlanQuantity() != null ? createDTO.getPlanQuantity() : BigDecimal.ZERO);
        workOrder.setActualQuantity(BigDecimal.ZERO);
        workOrder.setCompletedQuantity(BigDecimal.ZERO);
        workOrder.setScrappedQuantity(BigDecimal.ZERO);
        
        workOrder.setPlanStartDate(createDTO.getPlanStartDate());
        workOrder.setPlanEndDate(createDTO.getPlanEndDate());
        
        workOrder.setEquipmentId(createDTO.getEquipmentId());
        workOrder.setEquipmentCode(createDTO.getEquipmentCode());
        workOrder.setEquipmentName(createDTO.getEquipmentName());
        
        workOrder.setGroupId(createDTO.getGroupId());
        workOrder.setGroupCode(createDTO.getGroupCode());
        workOrder.setGroupName(createDTO.getGroupName());
        
        workOrder.setPriority(createDTO.getPriority() != null ? createDTO.getPriority() : 2);
        workOrder.setOrderSource(createDTO.getOrderSource());
        workOrder.setOrderSourceId(createDTO.getOrderSourceId());
        workOrder.setOrderSourceNo(createDTO.getOrderSourceNo());
        workOrder.setDeliveryDate(createDTO.getDeliveryDate());
        workOrder.setCompletionRate(BigDecimal.ZERO);
        
        workOrder.setRemark(createDTO.getRemark());
        workOrder.setStatus(STATUS_DRAFT);
        
        workOrderMapper.insert(workOrder);
        
        createWorkOrderLog(workOrder, null, STATUS_DRAFT, "创建工单", "系统创建工单", null, "admin");
        
        log.info("创建工单成功: {}", workOrderNo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateWorkOrder(WorkOrderUpdateDTO updateDTO) {
        ErpWorkOrder workOrder = workOrderMapper.selectById(updateDTO.getId());
        if (workOrder == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        if (updateDTO.getWorkOrderName() != null) {
            workOrder.setWorkOrderName(updateDTO.getWorkOrderName());
        }
        if (updateDTO.getWorkOrderType() != null) {
            workOrder.setWorkOrderType(updateDTO.getWorkOrderType());
        }
        if (updateDTO.getProductId() != null) {
            workOrder.setProductId(updateDTO.getProductId());
        }
        if (updateDTO.getProductCode() != null) {
            workOrder.setProductCode(updateDTO.getProductCode());
        }
        if (updateDTO.getProductName() != null) {
            workOrder.setProductName(updateDTO.getProductName());
        }
        if (updateDTO.getSpecification() != null) {
            workOrder.setSpecification(updateDTO.getSpecification());
        }
        if (updateDTO.getUnit() != null) {
            workOrder.setUnit(updateDTO.getUnit());
        }
        if (updateDTO.getPlanQuantity() != null) {
            workOrder.setPlanQuantity(updateDTO.getPlanQuantity());
        }
        if (updateDTO.getPlanStartDate() != null) {
            workOrder.setPlanStartDate(updateDTO.getPlanStartDate());
        }
        if (updateDTO.getPlanEndDate() != null) {
            workOrder.setPlanEndDate(updateDTO.getPlanEndDate());
        }
        if (updateDTO.getEquipmentId() != null) {
            workOrder.setEquipmentId(updateDTO.getEquipmentId());
        }
        if (updateDTO.getEquipmentCode() != null) {
            workOrder.setEquipmentCode(updateDTO.getEquipmentCode());
        }
        if (updateDTO.getEquipmentName() != null) {
            workOrder.setEquipmentName(updateDTO.getEquipmentName());
        }
        if (updateDTO.getGroupId() != null) {
            workOrder.setGroupId(updateDTO.getGroupId());
        }
        if (updateDTO.getGroupCode() != null) {
            workOrder.setGroupCode(updateDTO.getGroupCode());
        }
        if (updateDTO.getGroupName() != null) {
            workOrder.setGroupName(updateDTO.getGroupName());
        }
        if (updateDTO.getPriority() != null) {
            workOrder.setPriority(updateDTO.getPriority());
        }
        if (updateDTO.getDeliveryDate() != null) {
            workOrder.setDeliveryDate(updateDTO.getDeliveryDate());
        }
        if (updateDTO.getRemark() != null) {
            workOrder.setRemark(updateDTO.getRemark());
        }
        
        workOrderMapper.updateById(workOrder);
        
        log.info("更新工单成功: {}", workOrder.getWorkOrderNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteWorkOrder(Long id) {
        ErpWorkOrder workOrder = workOrderMapper.selectById(id);
        if (workOrder == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (workOrder.getStatus() != STATUS_DRAFT) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "只能删除草稿状态的工单");
        }
        
        workOrderMapper.deleteById(id);
        
        log.info("删除工单成功: {}", workOrder.getWorkOrderNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void submitApproval(Long id) {
        ErpWorkOrder workOrder = workOrderMapper.selectById(id);
        if (workOrder == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (workOrder.getStatus() != STATUS_DRAFT) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "只有草稿状态的工单才能提交审批");
        }
        
        int fromStatus = workOrder.getStatus();
        workOrder.setStatus(STATUS_PENDING_APPROVAL);
        workOrderMapper.updateById(workOrder);
        
        createWorkOrderLog(workOrder, fromStatus, STATUS_PENDING_APPROVAL, "提交审批", "提交审批等待审核", null, "admin");
        
        log.info("工单提交审批成功: {}", workOrder.getWorkOrderNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void approval(WorkOrderApprovalDTO approvalDTO) {
        List<Long> ids = approvalDTO.getIds();
        if (CollectionUtils.isEmpty(ids) && approvalDTO.getId() != null) {
            ids = List.of(approvalDTO.getId());
        }
        
        if (CollectionUtils.isEmpty(ids)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "请选择工单");
        }
        
        for (Long id : ids) {
            ErpWorkOrder workOrder = workOrderMapper.selectById(id);
            if (workOrder == null) {
                continue;
            }
            
            if (workOrder.getStatus() != STATUS_PENDING_APPROVAL) {
                continue;
            }
            
            int fromStatus = workOrder.getStatus();
            if (Boolean.TRUE.equals(approvalDTO.getApproved())) {
                workOrder.setStatus(STATUS_APPROVED);
                workOrder.setApprovalTime(LocalDateTime.now());
                workOrder.setApprovalOpinion(approvalDTO.getApprovalOpinion());
                createWorkOrderLog(workOrder, fromStatus, STATUS_APPROVED, "审批通过", approvalDTO.getApprovalOpinion() != null ? approvalDTO.getApprovalOpinion() : "审批通过", null, "admin");
            } else {
                workOrder.setStatus(STATUS_DRAFT);
                workOrder.setApprovalTime(LocalDateTime.now());
                workOrder.setApprovalOpinion(approvalDTO.getApprovalOpinion());
                createWorkOrderLog(workOrder, fromStatus, STATUS_DRAFT, "审批驳回", approvalDTO.getApprovalOpinion() != null ? approvalDTO.getApprovalOpinion() : "审批驳回", null, "admin");
            }
            
            workOrderMapper.updateById(workOrder);
            
            log.info("工单审批成功: {}", workOrder.getWorkOrderNo());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void issue(WorkOrderIssueDTO issueDTO) {
        List<Long> ids = issueDTO.getIds();
        if (CollectionUtils.isEmpty(ids) && issueDTO.getId() != null) {
            ids = List.of(issueDTO.getId());
        }
        
        if (CollectionUtils.isEmpty(ids)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "请选择工单");
        }
        
        for (Long id : ids) {
            ErpWorkOrder workOrder = workOrderMapper.selectById(id);
            if (workOrder == null) {
                continue;
            }
            
            if (workOrder.getStatus() != STATUS_APPROVED) {
                continue;
            }
            
            int fromStatus = workOrder.getStatus();
            workOrder.setStatus(STATUS_ISSUED);
            workOrder.setActualStartDate(issueDTO.getActualStartDate() != null ? 
                LocalDate.parse(issueDTO.getActualStartDate()) : LocalDate.now());
            workOrderMapper.updateById(workOrder);
            
            createWorkOrderLog(workOrder, fromStatus, STATUS_ISSUED, "下发工单", issueDTO.getRemark() != null ? issueDTO.getRemark() : "工单已下发", null, "admin");
            
            log.info("工单下发成功: {}", workOrder.getWorkOrderNo());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void pick(WorkOrderPickDTO pickDTO) {
        ErpWorkOrder workOrder = workOrderMapper.selectById(pickDTO.getWorkOrderId());
        if (workOrder == null && pickDTO.getWorkOrderId() != null) {
            workOrder = workOrderMapper.selectById(pickDTO.getWorkOrderId());
        }
        
        if (workOrder == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "工单不存在");
        }
        
        if (workOrder.getStatus() != STATUS_ISSUED && workOrder.getStatus() != STATUS_PICKING) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "只有已下发或领料中的工单才能领料");
        }
        
        int fromStatus = workOrder.getStatus();
        workOrder.setStatus(STATUS_PICKING);
        workOrderMapper.updateById(workOrder);
        
        createWorkOrderLog(workOrder, fromStatus, STATUS_PICKING, "领料", pickDTO.getRemark() != null ? pickDTO.getRemark() : "领料操作", null, "admin");
        
        log.info("工单领料成功: {}", workOrder.getWorkOrderNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void startProduction(Long id) {
        ErpWorkOrder workOrder = workOrderMapper.selectById(id);
        if (workOrder == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (workOrder.getStatus() != STATUS_PICKING) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "只有领料中的工单才能开始生产");
        }
        
        int fromStatus = workOrder.getStatus();
        workOrder.setStatus(STATUS_IN_PRODUCTION);
        workOrder.setActualStartDate(LocalDate.now());
        workOrderMapper.updateById(workOrder);
        
        createWorkOrderLog(workOrder, fromStatus, STATUS_IN_PRODUCTION, "开始生产", "工单开始生产", null, "admin");
        
        log.info("工单开始生产: {}", workOrder.getWorkOrderNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void report(WorkOrderReportDTO reportDTO) {
        ErpWorkOrder workOrder = workOrderMapper.selectById(reportDTO.getWorkOrderId());
        if (workOrder == null && reportDTO.getWorkOrderId() != null) {
            workOrder = workOrderMapper.selectById(reportDTO.getWorkOrderId());
        }
        
        if (workOrder == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "工单不存在");
        }
        
        if (workOrder.getStatus() != STATUS_IN_PRODUCTION && workOrder.getStatus() != STATUS_REPORTING) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "只有生产中或报工中的工单才能报工");
        }
        
        int fromStatus = workOrder.getStatus();
        workOrder.setStatus(STATUS_REPORTING);
        
        BigDecimal completedQuantity = workOrder.getCompletedQuantity() != null ? 
            workOrder.getCompletedQuantity() : BigDecimal.ZERO;
        BigDecimal scrappedQuantity = workOrder.getScrappedQuantity() != null ? 
            workOrder.getScrappedQuantity() : BigDecimal.ZERO;
        
        if (reportDTO.getReportQuantity() != null) {
            completedQuantity = completedQuantity.add(reportDTO.getReportQuantity());
        }
        if (reportDTO.getScrappedQuantity() != null) {
            scrappedQuantity = scrappedQuantity.add(reportDTO.getScrappedQuantity());
        }
        
        workOrder.setCompletedQuantity(completedQuantity);
        workOrder.setScrappedQuantity(scrappedQuantity);
        
        if (workOrder.getPlanQuantity() != null && workOrder.getPlanQuantity().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal completionRate = completedQuantity.divide(workOrder.getPlanQuantity(), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
            workOrder.setCompletionRate(completionRate);
        }
        
        workOrderMapper.updateById(workOrder);
        
        createWorkOrderLog(workOrder, fromStatus, STATUS_REPORTING, "报工", reportDTO.getRemark() != null ? reportDTO.getRemark() : "报工操作", 
            reportDTO.getReportQuantity(), "admin");
        
        log.info("工单报工成功: {}", workOrder.getWorkOrderNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void pendingStorage(Long id) {
        ErpWorkOrder workOrder = workOrderMapper.selectById(id);
        if (workOrder == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (workOrder.getStatus() != STATUS_REPORTING) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "只有报工中的工单才能转待入库");
        }
        
        int fromStatus = workOrder.getStatus();
        workOrder.setStatus(STATUS_PENDING_STORAGE);
        workOrderMapper.updateById(workOrder);
        
        createWorkOrderLog(workOrder, fromStatus, STATUS_PENDING_STORAGE, "待入库", "工单等待入库", null, "admin");
        
        log.info("工单转待入库: {}", workOrder.getWorkOrderNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void complete(WorkOrderCompleteDTO completeDTO) {
        ErpWorkOrder workOrder = workOrderMapper.selectById(completeDTO.getWorkOrderId());
        if (workOrder == null && completeDTO.getWorkOrderId() != null) {
            workOrder = workOrderMapper.selectById(completeDTO.getWorkOrderId());
        }
        
        if (workOrder == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "工单不存在");
        }
        
        if (workOrder.getStatus() != STATUS_PENDING_STORAGE) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "只有待入库状态的工单才能完工入库");
        }
        
        int fromStatus = workOrder.getStatus();
        workOrder.setStatus(STATUS_COMPLETED);
        workOrder.setActualEndDate(completeDTO.getActualEndDate() != null ? 
            completeDTO.getActualEndDate() : LocalDate.now());
        
        if (completeDTO.getCompleteQuantity() != null) {
            workOrder.setActualQuantity(completeDTO.getCompleteQuantity());
            workOrder.setCompletedQuantity(completeDTO.getCompleteQuantity());
            workOrder.setCompletionRate(new BigDecimal("100"));
        }
        
        workOrderMapper.updateById(workOrder);
        
        createWorkOrderLog(workOrder, fromStatus, STATUS_COMPLETED, "完工入库", completeDTO.getRemark() != null ? completeDTO.getRemark() : "工单已完工入库", 
            completeDTO.getCompleteQuantity(), "admin");
        
        log.info("工单完工入库成功: {}", workOrder.getWorkOrderNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long id) {
        ErpWorkOrder workOrder = workOrderMapper.selectById(id);
        if (workOrder == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (workOrder.getStatus() == STATUS_COMPLETED) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "已完工的工单不能取消");
        }
        
        int fromStatus = workOrder.getStatus();
        workOrder.setStatus(STATUS_CANCELLED);
        workOrderMapper.updateById(workOrder);
        
        createWorkOrderLog(workOrder, fromStatus, STATUS_CANCELLED, "取消工单", "工单已取消", null, "admin");
        
        log.info("工单取消成功: {}", workOrder.getWorkOrderNo());
    }

    public List<WorkOrderLogVO> getWorkOrderLogs(Long workOrderId) {
        List<ErpWorkOrderLog> logs = workOrderLogMapper.selectList(
            new LambdaQueryWrapper<ErpWorkOrderLog>()
                .eq(ErpWorkOrderLog::getWorkOrderId, workOrderId)
                .orderByDesc(ErpWorkOrderLog::getOperationTime)
        );
        
        return logs.stream().map(log -> {
            WorkOrderLogVO vo = new WorkOrderLogVO();
            vo.setId(log.getId());
            vo.setWorkOrderId(log.getWorkOrderId());
            vo.setWorkOrderNo(log.getWorkOrderNo());
            vo.setFromStatus(log.getFromStatus());
            vo.setFromStatusName(log.getFromStatusName());
            vo.setToStatus(log.getToStatus());
            vo.setToStatusName(log.getToStatusName());
            vo.setOperationType(log.getOperationType());
            vo.setOperationName(log.getOperationName());
            vo.setOperationQuantity(log.getOperationQuantity());
            vo.setOperator(log.getOperator());
            vo.setOperatorName(log.getOperatorName());
            vo.setOperationTime(log.getOperationTime());
            vo.setOperationRemark(log.getOperationRemark());
            return vo;
        }).collect(Collectors.toList());
    }

    public WorkOrderDashboardVO getDashboard() {
        WorkOrderDashboardVO dashboard = new WorkOrderDashboardVO();
        
        Long totalCount = workOrderMapper.selectCount(null);
        dashboard.setTotalCount(totalCount != null ? totalCount : 0L);
        
        dashboard.setDraftCount(getCountByStatus(STATUS_DRAFT));
        dashboard.setPendingApprovalCount(getCountByStatus(STATUS_PENDING_APPROVAL));
        dashboard.setApprovedCount(getCountByStatus(STATUS_APPROVED));
        dashboard.setIssuedCount(getCountByStatus(STATUS_ISSUED));
        dashboard.setPickingCount(getCountByStatus(STATUS_PICKING));
        dashboard.setInProductionCount(getCountByStatus(STATUS_IN_PRODUCTION));
        dashboard.setReportingCount(getCountByStatus(STATUS_REPORTING));
        dashboard.setPendingStorageCount(getCountByStatus(STATUS_PENDING_STORAGE));
        dashboard.setCompletedCount(getCountByStatus(STATUS_COMPLETED));
        dashboard.setCancelledCount(getCountByStatus(STATUS_CANCELLED));
        
        List<ErpWorkOrder> allWorkOrders = workOrderMapper.selectList(null);
        if (!CollectionUtils.isEmpty(allWorkOrders)) {
            BigDecimal totalRate = BigDecimal.ZERO;
            int count = 0;
            for (ErpWorkOrder wo : allWorkOrders) {
                if (wo.getCompletionRate() != null) {
                    totalRate = totalRate.add(wo.getCompletionRate());
                    count++;
                }
            }
            if (count > 0) {
                dashboard.setAvgCompletionRate(totalRate.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP));
            } else {
                dashboard.setAvgCompletionRate(BigDecimal.ZERO);
            }
        } else {
            dashboard.setAvgCompletionRate(BigDecimal.ZERO);
        }
        
        List<WorkOrderDashboardVO.StatusStatVO> statusStats = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : STATUS_NAMES.entrySet()) {
            Long count = getCountByStatus(entry.getKey());
            if (count > 0) {
                BigDecimal percentage = totalCount > 0 ? 
                    new BigDecimal(count).divide(new BigDecimal(totalCount), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100")) : BigDecimal.ZERO;
                statusStats.add(WorkOrderDashboardVO.StatusStatVO.builder()
                    .status(entry.getKey())
                    .statusName(entry.getValue())
                    .count(count)
                    .percentage(percentage)
                    .build());
            }
        }
        dashboard.setStatusStats(statusStats);
        
        List<WorkOrderDashboardVO.PriorityStatVO> priorityStats = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : PRIORITY_NAMES.entrySet()) {
            Long count = getCountByPriority(entry.getKey());
            if (count > 0) {
                BigDecimal percentage = totalCount > 0 ? 
                    new BigDecimal(count).divide(new BigDecimal(totalCount), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100")) : BigDecimal.ZERO;
                priorityStats.add(WorkOrderDashboardVO.PriorityStatVO.builder()
                    .priority(entry.getKey())
                    .priorityName(entry.getValue())
                    .count(count)
                    .percentage(percentage)
                    .build());
            }
        }
        dashboard.setPriorityStats(priorityStats);
        
        List<ErpWorkOrder> urgentWorkOrders = workOrderMapper.selectList(
            new LambdaQueryWrapper<ErpWorkOrder>()
                .ne(ErpWorkOrder::getStatus, STATUS_COMPLETED)
                .ne(ErpWorkOrder::getStatus, STATUS_CANCELLED)
                .isNotNull(ErpWorkOrder::getDeliveryDate)
                .orderByAsc(ErpWorkOrder::getDeliveryDate)
                .last("LIMIT 10")
        );
        dashboard.setUrgentWorkOrders(urgentWorkOrders.stream().map(this::convertToVO).collect(Collectors.toList()));
        
        List<ErpWorkOrder> recentWorkOrders = workOrderMapper.selectList(
            new LambdaQueryWrapper<ErpWorkOrder>()
                .orderByDesc(ErpWorkOrder::getCreateTime)
                .last("LIMIT 10")
        );
        dashboard.setRecentWorkOrders(recentWorkOrders.stream().map(this::convertToVO).collect(Collectors.toList()));
        
        return dashboard;
    }

    private Long getCountByStatus(Integer status) {
        Long count = workOrderMapper.selectCount(
            new LambdaQueryWrapper<ErpWorkOrder>().eq(ErpWorkOrder::getStatus, status)
        );
        return count != null ? count : 0L;
    }

    private Long getCountByPriority(Integer priority) {
        Long count = workOrderMapper.selectCount(
            new LambdaQueryWrapper<ErpWorkOrder>().eq(ErpWorkOrder::getPriority, priority)
        );
        return count != null ? count : 0L;
    }

    private void createWorkOrderLog(ErpWorkOrder workOrder, Integer fromStatus, Integer toStatus, 
            String operationType, String operationRemark, BigDecimal operationQuantity, String operator) {
        ErpWorkOrderLog log = new ErpWorkOrderLog();
        log.setWorkOrderId(workOrder.getId());
        log.setWorkOrderNo(workOrder.getWorkOrderNo());
        log.setFromStatus(fromStatus);
        log.setFromStatusName(fromStatus != null ? STATUS_NAMES.get(fromStatus) : null);
        log.setToStatus(toStatus);
        log.setToStatusName(STATUS_NAMES.get(toStatus));
        log.setOperationType(operationType);
        log.setOperationName(operationType);
        log.setOperationQuantity(operationQuantity);
        log.setOperator(operator);
        log.setOperatorName(operator);
        log.setOperationTime(LocalDateTime.now());
        log.setOperationRemark(operationRemark);
        workOrderLogMapper.insert(log);
    }

    private String generateWorkOrderNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        Long count = workOrderMapper.selectCount(
            new LambdaQueryWrapper<ErpWorkOrder>()
                .likeRight(ErpWorkOrder::getWorkOrderNo, "WO" + dateStr)
        );
        
        long seq = (count != null ? count : 0) + 1;
        return String.format("WO%s%04d", dateStr, seq);
    }

    private WorkOrderVO convertToVO(ErpWorkOrder workOrder) {
        String statusName = STATUS_NAMES.get(workOrder.getStatus());
        if (statusName == null) {
            statusName = "未知";
        }
        
        String priorityName = PRIORITY_NAMES.get(workOrder.getPriority());
        if (priorityName == null) {
            priorityName = "未知";
        }
        
        return WorkOrderVO.builder()
            .id(workOrder.getId())
            .workOrderNo(workOrder.getWorkOrderNo())
            .workOrderName(workOrder.getWorkOrderName())
            .workOrderType(workOrder.getWorkOrderType())
            .sourceMpsId(workOrder.getSourceMpsId())
            .sourceMpsNo(workOrder.getSourceMpsNo())
            .productId(workOrder.getProductId())
            .productCode(workOrder.getProductCode())
            .productName(workOrder.getProductName())
            .specification(workOrder.getSpecification())
            .unit(workOrder.getUnit())
            .planQuantity(workOrder.getPlanQuantity())
            .actualQuantity(workOrder.getActualQuantity())
            .completedQuantity(workOrder.getCompletedQuantity())
            .scrappedQuantity(workOrder.getScrappedQuantity())
            .planStartDate(workOrder.getPlanStartDate())
            .planEndDate(workOrder.getPlanEndDate())
            .actualStartDate(workOrder.getActualStartDate())
            .actualEndDate(workOrder.getActualEndDate())
            .equipmentId(workOrder.getEquipmentId())
            .equipmentCode(workOrder.getEquipmentCode())
            .equipmentName(workOrder.getEquipmentName())
            .groupId(workOrder.getGroupId())
            .groupCode(workOrder.getGroupCode())
            .groupName(workOrder.getGroupName())
            .priority(workOrder.getPriority())
            .priorityName(priorityName)
            .orderSource(workOrder.getOrderSource())
            .orderSourceId(workOrder.getOrderSourceId())
            .orderSourceNo(workOrder.getOrderSourceNo())
            .deliveryDate(workOrder.getDeliveryDate())
            .completionRate(workOrder.getCompletionRate())
            .approvalUserId(workOrder.getApprovalUserId())
            .approvalUserName(workOrder.getApprovalUserName())
            .approvalTime(workOrder.getApprovalTime())
            .approvalOpinion(workOrder.getApprovalOpinion())
            .remark(workOrder.getRemark())
            .status(workOrder.getStatus())
            .statusName(statusName)
            .createBy(workOrder.getCreateBy())
            .createTime(workOrder.getCreateTime())
            .updateBy(workOrder.getUpdateBy())
            .updateTime(workOrder.getUpdateTime())
            .build();
    }
}
