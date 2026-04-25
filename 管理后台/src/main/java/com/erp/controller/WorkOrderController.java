package com.erp.controller;

import com.baserbac.common.result.PageResult;
import com.baserbac.common.result.R;
import com.erp.dto.WorkOrderApprovalDTO;
import com.erp.dto.WorkOrderCompleteDTO;
import com.erp.dto.WorkOrderCreateDTO;
import com.erp.dto.WorkOrderIssueDTO;
import com.erp.dto.WorkOrderPickDTO;
import com.erp.dto.WorkOrderQueryDTO;
import com.erp.dto.WorkOrderReportDTO;
import com.erp.dto.WorkOrderUpdateDTO;
import com.erp.service.WorkOrderService;
import com.erp.vo.WorkOrderDashboardVO;
import com.erp.vo.WorkOrderLogVO;
import com.erp.vo.WorkOrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "工单管理")
@RestController
@RequestMapping("/api/erp/work-orders")
@RequiredArgsConstructor
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    @Operation(summary = "分页查询工单列表")
    @GetMapping("/list")
    public R<PageResult<WorkOrderVO>> list(WorkOrderQueryDTO queryDTO) {
        PageResult<WorkOrderVO> result = workOrderService.pageWorkOrders(queryDTO);
        return R.success(result);
    }

    @Operation(summary = "根据ID查询工单详情")
    @GetMapping("/{id}")
    public R<WorkOrderVO> getById(@PathVariable Long id) {
        WorkOrderVO vo = workOrderService.getWorkOrderById(id);
        return R.success(vo);
    }

    @Operation(summary = "创建工单")
    @PostMapping
    public R<Void> create(@RequestBody @Validated WorkOrderCreateDTO createDTO) {
        workOrderService.createWorkOrder(createDTO);
        return R.success();
    }

    @Operation(summary = "更新工单")
    @PutMapping
    public R<Void> update(@RequestBody @Validated WorkOrderUpdateDTO updateDTO) {
        workOrderService.updateWorkOrder(updateDTO);
        return R.success();
    }

    @Operation(summary = "删除工单")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        workOrderService.deleteWorkOrder(id);
        return R.success();
    }

    @Operation(summary = "提交审批")
    @PostMapping("/submit-approval/{id}")
    public R<Void> submitApproval(@PathVariable Long id) {
        workOrderService.submitApproval(id);
        return R.success();
    }

    @Operation(summary = "批量提交审批")
    @PostMapping("/submit-approval/batch")
    public R<Void> submitApprovalBatch(@RequestBody List<Long> ids) {
        for (Long id : ids) {
            workOrderService.submitApproval(id);
        }
        return R.success();
    }

    @Operation(summary = "审批工单")
    @PostMapping("/approval")
    public R<Void> approval(@RequestBody WorkOrderApprovalDTO approvalDTO) {
        workOrderService.approval(approvalDTO);
        return R.success();
    }

    @Operation(summary = "下发工单")
    @PostMapping("/issue")
    public R<Void> issue(@RequestBody WorkOrderIssueDTO issueDTO) {
        workOrderService.issue(issueDTO);
        return R.success();
    }

    @Operation(summary = "领料")
    @PostMapping("/pick")
    public R<Void> pick(@RequestBody WorkOrderPickDTO pickDTO) {
        workOrderService.pick(pickDTO);
        return R.success();
    }

    @Operation(summary = "开始生产")
    @PostMapping("/start-production/{id}")
    public R<Void> startProduction(@PathVariable Long id) {
        workOrderService.startProduction(id);
        return R.success();
    }

    @Operation(summary = "报工")
    @PostMapping("/report")
    public R<Void> report(@RequestBody WorkOrderReportDTO reportDTO) {
        workOrderService.report(reportDTO);
        return R.success();
    }

    @Operation(summary = "转待入库")
    @PostMapping("/pending-storage/{id}")
    public R<Void> pendingStorage(@PathVariable Long id) {
        workOrderService.pendingStorage(id);
        return R.success();
    }

    @Operation(summary = "完工入库")
    @PostMapping("/complete")
    public R<Void> complete(@RequestBody WorkOrderCompleteDTO completeDTO) {
        workOrderService.complete(completeDTO);
        return R.success();
    }

    @Operation(summary = "取消工单")
    @PostMapping("/cancel/{id}")
    public R<Void> cancel(@PathVariable Long id) {
        workOrderService.cancel(id);
        return R.success();
    }

    @Operation(summary = "查询工单流程日志")
    @GetMapping("/logs/{workOrderId}")
    public R<List<WorkOrderLogVO>> getLogs(@PathVariable Long workOrderId) {
        List<WorkOrderLogVO> logs = workOrderService.getWorkOrderLogs(workOrderId);
        return R.success(logs);
    }

    @Operation(summary = "获取工单看板统计")
    @GetMapping("/dashboard")
    public R<WorkOrderDashboardVO> getDashboard() {
        WorkOrderDashboardVO dashboard = workOrderService.getDashboard();
        return R.success(dashboard);
    }
}
