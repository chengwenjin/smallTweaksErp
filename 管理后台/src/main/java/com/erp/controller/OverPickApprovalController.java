package com.erp.controller;

import com.baserbac.annotation.OperationLog;
import com.baserbac.common.result.PageResult;
import com.baserbac.common.result.R;
import com.erp.dto.OverPickApprovalCreateDTO;
import com.erp.dto.OverPickApprovalDTO;
import com.erp.dto.OverPickApprovalQueryDTO;
import com.erp.dto.OverPickApprovalUpdateDTO;
import com.erp.service.OverPickApprovalService;
import com.erp.vo.OverPickApprovalVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "超领审批管理")
@RestController
@RequestMapping("/api/erp/over-pick-approvals")
@RequiredArgsConstructor
public class OverPickApprovalController {

    private final OverPickApprovalService overPickApprovalService;

    @Operation(summary = "分页查询超领审批单列表")
    @GetMapping("/list")
    public R<PageResult<OverPickApprovalVO>> list(OverPickApprovalQueryDTO queryDTO) {
        PageResult<OverPickApprovalVO> result = overPickApprovalService.pageOverPickApprovals(queryDTO);
        return R.success(result);
    }

    @Operation(summary = "根据ID查询超领审批单详情")
    @GetMapping("/{id}")
    public R<OverPickApprovalVO> getById(@PathVariable Long id) {
        OverPickApprovalVO vo = overPickApprovalService.getOverPickApprovalById(id);
        return R.success(vo);
    }

    @OperationLog(module = "超领审批管理", value = "新增超领审批单")
    @Operation(summary = "创建超领审批单")
    @PostMapping
    public R<Void> create(@RequestBody @Validated OverPickApprovalCreateDTO createDTO) {
        overPickApprovalService.createOverPickApproval(createDTO);
        return R.success();
    }

    @OperationLog(module = "超领审批管理", value = "编辑超领审批单")
    @Operation(summary = "更新超领审批单")
    @PutMapping
    public R<Void> update(@RequestBody @Validated OverPickApprovalUpdateDTO updateDTO) {
        overPickApprovalService.updateOverPickApproval(updateDTO);
        return R.success();
    }

    @OperationLog(module = "超领审批管理", value = "删除超领审批单")
    @Operation(summary = "删除超领审批单")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        overPickApprovalService.deleteOverPickApproval(id);
        return R.success();
    }

    @OperationLog(module = "超领审批管理", value = "提交审批")
    @Operation(summary = "提交审批")
    @PostMapping("/submit-approval/{id}")
    public R<Void> submitApproval(@PathVariable Long id) {
        overPickApprovalService.submitApproval(id);
        return R.success();
    }

    @OperationLog(module = "超领审批管理", value = "审批")
    @Operation(summary = "审批超领申请")
    @PostMapping("/approval")
    public R<Void> approval(@RequestBody @Validated OverPickApprovalDTO approvalDTO) {
        overPickApprovalService.approval(approvalDTO);
        return R.success();
    }

    @OperationLog(module = "超领审批管理", value = "撤销")
    @Operation(summary = "撤销超领申请")
    @PostMapping("/cancel/{id}")
    public R<Void> cancel(@PathVariable Long id) {
        overPickApprovalService.cancel(id);
        return R.success();
    }
}
