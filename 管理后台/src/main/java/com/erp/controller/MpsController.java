package com.erp.controller;

import com.baserbac.annotation.OperationLog;
import com.baserbac.common.result.R;
import com.baserbac.common.result.PageResult;
import com.erp.dto.MpsCreateDTO;
import com.erp.dto.MpsQueryDTO;
import com.erp.dto.MpsUpdateDTO;
import com.erp.service.MpsService;
import com.erp.vo.MpsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "主生产计划管理")
@RestController
@RequestMapping("/api/erp/mps")
@RequiredArgsConstructor
public class MpsController {

    private final MpsService mpsService;

    @Operation(summary = "分页查询主生产计划列表")
    @GetMapping
    public R<PageResult<MpsVO>> pageMps(MpsQueryDTO queryDTO) {
        return R.success(mpsService.pageMps(queryDTO));
    }

    @Operation(summary = "根据ID查询主生产计划")
    @GetMapping("/{id}")
    public R<MpsVO> getMpsById(@PathVariable Long id) {
        return R.success(mpsService.getMpsById(id));
    }

    @OperationLog("创建主生产计划")
    @Operation(summary = "创建主生产计划")
    @PostMapping
    public R<Void> createMps(@RequestBody MpsCreateDTO createDTO) {
        mpsService.createMps(createDTO);
        return R.success();
    }

    @OperationLog("更新主生产计划")
    @Operation(summary = "更新主生产计划")
    @PutMapping("/{id}")
    public R<Void> updateMps(@PathVariable Long id, @RequestBody MpsUpdateDTO updateDTO) {
        updateDTO.setId(id);
        mpsService.updateMps(updateDTO);
        return R.success();
    }

    @OperationLog("删除主生产计划")
    @Operation(summary = "删除主生产计划")
    @DeleteMapping("/{id}")
    public R<Void> deleteMps(@PathVariable Long id) {
        mpsService.deleteMps(id);
        return R.success();
    }

    @OperationLog("更新主生产计划状态")
    @Operation(summary = "更新主生产计划状态")
    @PutMapping("/{id}/status")
    public R<Void> updateMpsStatus(@PathVariable Long id, @RequestParam Integer status) {
        mpsService.updateMpsStatus(id, status);
        return R.success();
    }

    @OperationLog("确认主生产计划")
    @Operation(summary = "确认主生产计划")
    @PostMapping("/{id}/confirm")
    public R<Void> confirmMps(@PathVariable Long id) {
        mpsService.updateMpsStatus(id, 2);
        return R.success();
    }

    @OperationLog("取消主生产计划")
    @Operation(summary = "取消主生产计划")
    @PostMapping("/{id}/cancel")
    public R<Void> cancelMps(@PathVariable Long id) {
        mpsService.updateMpsStatus(id, 5);
        return R.success();
    }

    @OperationLog("从净需求生成主生产计划")
    @Operation(summary = "从净需求生成主生产计划（产能平衡）")
    @PostMapping("/generate-from-net-requirement")
    public R<Void> generateMpsFromNetRequirement() {
        mpsService.generateMpsFromNetRequirement();
        return R.success();
    }

    @OperationLog("批量更新主生产计划状态")
    @Operation(summary = "批量更新主生产计划状态")
    @PutMapping("/batch/status")
    public R<Void> updateMpsStatusBatch(@RequestBody BatchStatusDTO dto) {
        if (dto.getIds() != null) {
            for (Long id : dto.getIds()) {
                mpsService.updateMpsStatus(id, dto.getStatus());
            }
        }
        return R.success();
    }

    @OperationLog("批量删除主生产计划")
    @Operation(summary = "批量删除主生产计划")
    @DeleteMapping("/batch")
    public R<Void> deleteMpsBatch(@RequestBody BatchDeleteDTO dto) {
        if (dto.getIds() != null) {
            for (Long id : dto.getIds()) {
                mpsService.deleteMps(id);
            }
        }
        return R.success();
    }

    @lombok.Data
    public static class BatchStatusDTO {
        private List<Long> ids;
        private Integer status;
    }

    @lombok.Data
    public static class BatchDeleteDTO {
        private List<Long> ids;
    }
}
