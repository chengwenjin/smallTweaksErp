package com.erp.controller;

import com.baserbac.annotation.OperationLog;
import com.baserbac.common.result.R;
import com.baserbac.common.result.PageResult;
import com.erp.dto.KittingAlertCreateDTO;
import com.erp.dto.KittingAlertQueryDTO;
import com.erp.dto.KittingAlertUpdateDTO;
import com.erp.service.KittingAlertService;
import com.erp.vo.KittingAlertVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "齐套预警管理")
@RestController
@RequestMapping("/api/erp/kitting-alerts")
@RequiredArgsConstructor
public class KittingAlertController {

    private final KittingAlertService kittingAlertService;

    @Operation(summary = "分页查询齐套预警列表")
    @GetMapping
    public R<PageResult<KittingAlertVO>> pageKittingAlerts(KittingAlertQueryDTO queryDTO) {
        return R.success(kittingAlertService.pageKittingAlerts(queryDTO));
    }

    @Operation(summary = "根据ID查询齐套预警")
    @GetMapping("/{id}")
    public R<KittingAlertVO> getKittingAlertById(@PathVariable Long id) {
        return R.success(kittingAlertService.getKittingAlertById(id));
    }

    @OperationLog("创建齐套预警")
    @Operation(summary = "创建齐套预警")
    @PostMapping
    public R<Void> createKittingAlert(@RequestBody KittingAlertCreateDTO createDTO) {
        kittingAlertService.createKittingAlert(createDTO);
        return R.success();
    }

    @OperationLog("更新齐套预警")
    @Operation(summary = "更新齐套预警")
    @PutMapping("/{id}")
    public R<Void> updateKittingAlert(@PathVariable Long id, @RequestBody KittingAlertUpdateDTO updateDTO) {
        updateDTO.setId(id);
        kittingAlertService.updateKittingAlert(updateDTO);
        return R.success();
    }

    @OperationLog("删除齐套预警")
    @Operation(summary = "删除齐套预警")
    @DeleteMapping("/{id}")
    public R<Void> deleteKittingAlert(@PathVariable Long id) {
        kittingAlertService.deleteKittingAlert(id);
        return R.success();
    }

    @OperationLog("更新齐套预警状态")
    @Operation(summary = "更新齐套预警状态")
    @PutMapping("/{id}/status")
    public R<Void> updateKittingAlertStatus(@PathVariable Long id, @RequestParam Integer status) {
        kittingAlertService.updateKittingAlertStatus(id, status);
        return R.success();
    }

    @OperationLog("计算齐套预警")
    @Operation(summary = "计算齐套预警")
    @PostMapping("/calculate")
    public R<Void> calculateKittingAlerts() {
        kittingAlertService.calculateKittingAlerts();
        return R.success();
    }

    @OperationLog("同步到SCM模块")
    @Operation(summary = "同步到SCM模块跟催")
    @PostMapping("/{id}/sync-to-scrm")
    public R<Void> syncToScrm(@PathVariable Long id) {
        kittingAlertService.syncToScrm(id);
        return R.success();
    }

    @OperationLog("批量同步到SCM模块")
    @Operation(summary = "批量同步到SCM模块跟催")
    @PostMapping("/batch/sync-to-scrm")
    public R<Void> syncToScrmBatch(@RequestBody BatchSyncDTO dto) {
        kittingAlertService.syncToScrmBatch(dto.getIds());
        return R.success();
    }

    @OperationLog("批量更新齐套预警状态")
    @Operation(summary = "批量更新齐套预警状态")
    @PutMapping("/batch/status")
    public R<Void> updateKittingAlertStatusBatch(@RequestBody BatchStatusDTO dto) {
        if (dto.getIds() != null) {
            for (Long id : dto.getIds()) {
                kittingAlertService.updateKittingAlertStatus(id, dto.getStatus());
            }
        }
        return R.success();
    }

    @OperationLog("批量删除齐套预警")
    @Operation(summary = "批量删除齐套预警")
    @DeleteMapping("/batch")
    public R<Void> deleteKittingAlertBatch(@RequestBody BatchDeleteDTO dto) {
        if (dto.getIds() != null) {
            for (Long id : dto.getIds()) {
                kittingAlertService.deleteKittingAlert(id);
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

    @lombok.Data
    public static class BatchSyncDTO {
        private List<Long> ids;
    }
}
