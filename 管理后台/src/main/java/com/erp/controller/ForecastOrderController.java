package com.erp.controller;

import com.baserbac.annotation.OperationLog;
import com.baserbac.common.result.R;
import com.baserbac.common.result.PageResult;
import com.erp.dto.ForecastOrderCreateDTO;
import com.erp.dto.ForecastOrderQueryDTO;
import com.erp.dto.ForecastOrderUpdateDTO;
import com.erp.service.ForecastOrderService;
import com.erp.vo.ForecastOrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "预测单管理")
@RestController
@RequestMapping("/api/erp/forecast-orders")
@RequiredArgsConstructor
public class ForecastOrderController {

    private final ForecastOrderService forecastOrderService;

    @Operation(summary = "分页查询预测单列表")
    @GetMapping
    public R<PageResult<ForecastOrderVO>> pageForecastOrders(ForecastOrderQueryDTO queryDTO) {
        return R.success(forecastOrderService.pageForecastOrders(queryDTO));
    }

    @Operation(summary = "根据ID查询预测单")
    @GetMapping("/{id}")
    public R<ForecastOrderVO> getForecastOrder(@PathVariable Long id) {
        return R.success(forecastOrderService.getForecastOrderById(id));
    }

    @OperationLog(module = "预测单管理", value = "新增预测单")
    @Operation(summary = "创建预测单")
    @PostMapping
    public R<Void> createForecastOrder(@Valid @RequestBody ForecastOrderCreateDTO createDTO) {
        forecastOrderService.createForecastOrder(createDTO);
        return R.success();
    }

    @OperationLog(module = "预测单管理", value = "编辑预测单")
    @Operation(summary = "更新预测单")
    @PutMapping("/{id}")
    public R<Void> updateForecastOrder(@PathVariable Long id, @Valid @RequestBody ForecastOrderUpdateDTO updateDTO) {
        updateDTO.setId(id);
        forecastOrderService.updateForecastOrder(updateDTO);
        return R.success();
    }

    @OperationLog(module = "预测单管理", value = "删除预测单")
    @Operation(summary = "删除预测单")
    @DeleteMapping("/{id}")
    public R<Void> deleteForecastOrder(@PathVariable Long id) {
        forecastOrderService.deleteForecastOrder(id);
        return R.success();
    }

    @OperationLog(module = "预测单管理", value = "更新预测单状态")
    @Operation(summary = "更新预测单状态")
    @PutMapping("/{id}/status")
    public R<Void> updateForecastOrderStatus(@PathVariable Long id, @RequestBody StatusDTO statusDTO) {
        forecastOrderService.updateForecastOrderStatus(id, statusDTO.getStatus());
        return R.success();
    }

    @lombok.Data
    public static class StatusDTO {
        private Integer status;
    }
}
