package com.erp.controller;

import com.baserbac.annotation.OperationLog;
import com.baserbac.common.result.R;
import com.baserbac.common.result.PageResult;
import com.erp.dto.DemandSourceQueryDTO;
import com.erp.service.DemandSourceService;
import com.erp.vo.DemandSourceVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "需求来源管理")
@RestController
@RequestMapping("/api/erp/demand-sources")
@RequiredArgsConstructor
public class DemandSourceController {

    private final DemandSourceService demandSourceService;

    @Operation(summary = "分页查询需求来源列表")
    @GetMapping
    public R<PageResult<DemandSourceVO>> pageDemandSources(DemandSourceQueryDTO queryDTO) {
        return R.success(demandSourceService.pageDemandSources(queryDTO));
    }

    @Operation(summary = "根据ID查询需求来源")
    @GetMapping("/{id}")
    public R<DemandSourceVO> getDemandSource(@PathVariable Long id) {
        return R.success(demandSourceService.getDemandSourceById(id));
    }

    @OperationLog(module = "需求来源管理", value = "从销售订单同步需求")
    @Operation(summary = "从销售订单同步需求来源")
    @PostMapping("/sync-sales")
    public R<Void> syncFromSalesOrders() {
        demandSourceService.syncFromSalesOrders();
        return R.success();
    }

    @OperationLog(module = "需求来源管理", value = "从预测单同步需求")
    @Operation(summary = "从预测单同步需求来源")
    @PostMapping("/sync-forecast")
    public R<Void> syncFromForecastOrders() {
        demandSourceService.syncFromForecastOrders();
        return R.success();
    }

    @OperationLog(module = "需求来源管理", value = "同步所有需求来源")
    @Operation(summary = "同步所有需求来源")
    @PostMapping("/sync-all")
    public R<Void> syncAllDemandSources() {
        demandSourceService.syncAllDemandSources();
        return R.success();
    }

    @OperationLog(module = "需求来源管理", value = "更新需求来源状态")
    @Operation(summary = "更新需求来源状态")
    @PutMapping("/{id}/status")
    public R<Void> updateDemandSourceStatus(@PathVariable Long id, @RequestBody StatusDTO statusDTO) {
        demandSourceService.updateDemandSourceStatus(id, statusDTO.getStatus());
        return R.success();
    }

    @OperationLog(module = "需求来源管理", value = "批量更新需求来源状态")
    @Operation(summary = "批量更新需求来源状态")
    @PutMapping("/batch/status")
    public R<Void> updateDemandSourceStatusBatch(@RequestBody BatchStatusDTO batchStatusDTO) {
        demandSourceService.updateDemandSourceStatusBatch(batchStatusDTO.getIds(), batchStatusDTO.getStatus());
        return R.success();
    }

    @OperationLog(module = "需求来源管理", value = "批量删除需求来源")
    @Operation(summary = "批量删除需求来源")
    @DeleteMapping("/batch")
    public R<Void> deleteDemandSourceBatch(@RequestBody BatchDeleteDTO batchDeleteDTO) {
        for (Long id : batchDeleteDTO.getIds()) {
            demandSourceService.deleteDemandSource(id);
        }
        return R.success();
    }

    @OperationLog(module = "需求来源管理", value = "删除需求来源")
    @Operation(summary = "删除需求来源")
    @DeleteMapping("/{id}")
    public R<Void> deleteDemandSource(@PathVariable Long id) {
        demandSourceService.deleteDemandSource(id);
        return R.success();
    }

    @lombok.Data
    public static class StatusDTO {
        private Integer status;
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
