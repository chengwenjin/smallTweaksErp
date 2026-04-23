package com.erp.controller;

import com.baserbac.annotation.OperationLog;
import com.baserbac.common.result.R;
import com.baserbac.common.result.PageResult;
import com.erp.dto.NetRequirementQueryDTO;
import com.erp.service.NetRequirementService;
import com.erp.vo.NetRequirementVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "净需求计算")
@RestController
@RequestMapping("/api/erp/net-requirements")
@RequiredArgsConstructor
public class NetRequirementController {

    private final NetRequirementService netRequirementService;

    @Operation(summary = "分页查询净需求列表")
    @GetMapping
    public R<PageResult<NetRequirementVO>> pageNetRequirements(NetRequirementQueryDTO queryDTO) {
        return R.success(netRequirementService.pageNetRequirements(queryDTO));
    }

    @Operation(summary = "根据ID查询净需求")
    @GetMapping("/{id}")
    public R<NetRequirementVO> getNetRequirement(@PathVariable Long id) {
        return R.success(netRequirementService.getNetRequirementById(id));
    }

    @OperationLog(module = "净需求计算", value = "执行净需求计算")
    @Operation(summary = "执行净需求计算")
    @PostMapping("/calculate")
    public R<Void> calculateNetRequirements() {
        netRequirementService.calculateNetRequirements();
        return R.success();
    }

    @OperationLog(module = "净需求计算", value = "确认净需求")
    @Operation(summary = "确认净需求")
    @PostMapping("/{id}/confirm")
    public R<Void> confirmNetRequirement(@PathVariable Long id) {
        netRequirementService.confirmNetRequirement(id);
        return R.success();
    }

    @OperationLog(module = "净需求计算", value = "更新净需求状态")
    @Operation(summary = "更新净需求状态")
    @PutMapping("/{id}/status")
    public R<Void> updateNetRequirementStatus(@PathVariable Long id, @RequestBody StatusDTO statusDTO) {
        netRequirementService.updateNetRequirementStatus(id, statusDTO.getStatus());
        return R.success();
    }

    @OperationLog(module = "净需求计算", value = "删除净需求")
    @Operation(summary = "删除净需求")
    @DeleteMapping("/{id}")
    public R<Void> deleteNetRequirement(@PathVariable Long id) {
        netRequirementService.deleteNetRequirement(id);
        return R.success();
    }

    @lombok.Data
    public static class StatusDTO {
        private Integer status;
    }
}
