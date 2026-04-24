package com.erp.controller;

import com.baserbac.annotation.OperationLog;
import com.baserbac.common.result.R;
import com.baserbac.common.result.PageResult;
import com.erp.dto.EquipmentCreateDTO;
import com.erp.dto.EquipmentQueryDTO;
import com.erp.dto.EquipmentUpdateDTO;
import com.erp.service.EquipmentService;
import com.erp.vo.EquipmentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "设备管理")
@RestController
@RequestMapping("/api/erp/equipments")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @Operation(summary = "分页查询设备列表")
    @GetMapping
    public R<PageResult<EquipmentVO>> pageEquipments(EquipmentQueryDTO queryDTO) {
        return R.success(equipmentService.pageEquipments(queryDTO));
    }

    @Operation(summary = "根据ID查询设备")
    @GetMapping("/{id}")
    public R<EquipmentVO> getEquipmentById(@PathVariable Long id) {
        return R.success(equipmentService.getEquipmentById(id));
    }

    @OperationLog("创建设备")
    @Operation(summary = "创建设备")
    @PostMapping
    public R<Void> createEquipment(@RequestBody EquipmentCreateDTO createDTO) {
        equipmentService.createEquipment(createDTO);
        return R.success();
    }

    @OperationLog("更新设备")
    @Operation(summary = "更新设备")
    @PutMapping("/{id}")
    public R<Void> updateEquipment(@PathVariable Long id, @RequestBody EquipmentUpdateDTO updateDTO) {
        updateDTO.setId(id);
        equipmentService.updateEquipment(updateDTO);
        return R.success();
    }

    @OperationLog("删除设备")
    @Operation(summary = "删除设备")
    @DeleteMapping("/{id}")
    public R<Void> deleteEquipment(@PathVariable Long id) {
        equipmentService.deleteEquipment(id);
        return R.success();
    }

    @OperationLog("更新设备状态")
    @Operation(summary = "更新设备状态")
    @PutMapping("/{id}/status")
    public R<Void> updateEquipmentStatus(@PathVariable Long id, @RequestParam Integer status) {
        equipmentService.updateEquipmentStatus(id, status);
        return R.success();
    }

    @OperationLog("批量更新设备状态")
    @Operation(summary = "批量更新设备状态")
    @PutMapping("/batch/status")
    public R<Void> updateEquipmentStatusBatch(@RequestBody BatchStatusDTO dto) {
        if (dto.getIds() != null) {
            for (Long id : dto.getIds()) {
                equipmentService.updateEquipmentStatus(id, dto.getStatus());
            }
        }
        return R.success();
    }

    @OperationLog("批量删除设备")
    @Operation(summary = "批量删除设备")
    @DeleteMapping("/batch")
    public R<Void> deleteEquipmentBatch(@RequestBody BatchDeleteDTO dto) {
        if (dto.getIds() != null) {
            for (Long id : dto.getIds()) {
                equipmentService.deleteEquipment(id);
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
