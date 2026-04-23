package com.erp.controller;

import com.baserbac.annotation.OperationLog;
import com.baserbac.common.result.R;
import com.baserbac.common.result.PageResult;
import com.erp.dto.InventoryCreateDTO;
import com.erp.dto.InventoryQueryDTO;
import com.erp.dto.InventoryUpdateDTO;
import com.erp.service.InventoryService;
import com.erp.vo.InventoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "库存管理")
@RestController
@RequestMapping("/api/erp/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "分页查询库存列表")
    @GetMapping
    public R<PageResult<InventoryVO>> pageInventories(InventoryQueryDTO queryDTO) {
        return R.success(inventoryService.pageInventories(queryDTO));
    }

    @Operation(summary = "根据ID查询库存")
    @GetMapping("/{id}")
    public R<InventoryVO> getInventory(@PathVariable Long id) {
        return R.success(inventoryService.getInventoryById(id));
    }

    @OperationLog(module = "库存管理", value = "新增库存")
    @Operation(summary = "创建库存")
    @PostMapping
    public R<Void> createInventory(@Valid @RequestBody InventoryCreateDTO createDTO) {
        inventoryService.createInventory(createDTO);
        return R.success();
    }

    @OperationLog(module = "库存管理", value = "编辑库存")
    @Operation(summary = "更新库存")
    @PutMapping("/{id}")
    public R<Void> updateInventory(@PathVariable Long id, @Valid @RequestBody InventoryUpdateDTO updateDTO) {
        updateDTO.setId(id);
        inventoryService.updateInventory(updateDTO);
        return R.success();
    }

    @OperationLog(module = "库存管理", value = "删除库存")
    @Operation(summary = "删除库存")
    @DeleteMapping("/{id}")
    public R<Void> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return R.success();
    }

    @OperationLog(module = "库存管理", value = "更新库存状态")
    @Operation(summary = "更新库存状态")
    @PutMapping("/{id}/status")
    public R<Void> updateInventoryStatus(@PathVariable Long id, @RequestBody StatusDTO statusDTO) {
        inventoryService.updateInventoryStatus(id, statusDTO.getStatus());
        return R.success();
    }

    @lombok.Data
    public static class StatusDTO {
        private Integer status;
    }
}
