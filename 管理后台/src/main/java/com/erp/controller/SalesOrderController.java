package com.erp.controller;

import com.baserbac.annotation.OperationLog;
import com.baserbac.common.result.R;
import com.baserbac.common.result.PageResult;
import com.erp.dto.SalesOrderCreateDTO;
import com.erp.dto.SalesOrderQueryDTO;
import com.erp.dto.SalesOrderUpdateDTO;
import com.erp.service.SalesOrderService;
import com.erp.vo.SalesOrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "销售订单管理")
@RestController
@RequestMapping("/api/erp/sales-orders")
@RequiredArgsConstructor
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    @Operation(summary = "分页查询销售订单列表")
    @GetMapping
    public R<PageResult<SalesOrderVO>> pageSalesOrders(SalesOrderQueryDTO queryDTO) {
        return R.success(salesOrderService.pageSalesOrders(queryDTO));
    }

    @Operation(summary = "根据ID查询销售订单")
    @GetMapping("/{id}")
    public R<SalesOrderVO> getSalesOrder(@PathVariable Long id) {
        return R.success(salesOrderService.getSalesOrderById(id));
    }

    @OperationLog(module = "销售订单管理", value = "新增销售订单")
    @Operation(summary = "创建销售订单")
    @PostMapping
    public R<Void> createSalesOrder(@Valid @RequestBody SalesOrderCreateDTO createDTO) {
        salesOrderService.createSalesOrder(createDTO);
        return R.success();
    }

    @OperationLog(module = "销售订单管理", value = "编辑销售订单")
    @Operation(summary = "更新销售订单")
    @PutMapping("/{id}")
    public R<Void> updateSalesOrder(@PathVariable Long id, @Valid @RequestBody SalesOrderUpdateDTO updateDTO) {
        updateDTO.setId(id);
        salesOrderService.updateSalesOrder(updateDTO);
        return R.success();
    }

    @OperationLog(module = "销售订单管理", value = "删除销售订单")
    @Operation(summary = "删除销售订单")
    @DeleteMapping("/{id}")
    public R<Void> deleteSalesOrder(@PathVariable Long id) {
        salesOrderService.deleteSalesOrder(id);
        return R.success();
    }

    @OperationLog(module = "销售订单管理", value = "更新销售订单状态")
    @Operation(summary = "更新销售订单状态")
    @PutMapping("/{id}/status")
    public R<Void> updateSalesOrderStatus(@PathVariable Long id, @RequestBody StatusDTO statusDTO) {
        salesOrderService.updateSalesOrderStatus(id, statusDTO.getStatus());
        return R.success();
    }

    @lombok.Data
    public static class StatusDTO {
        private Integer status;
    }
}
