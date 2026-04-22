package com.erp.controller;

import com.baserbac.annotation.OperationLog;
import com.baserbac.common.result.R;
import com.baserbac.common.result.PageResult;
import com.erp.dto.AlternativeMaterialCreateDTO;
import com.erp.dto.AlternativeMaterialQueryDTO;
import com.erp.dto.AlternativeMaterialUpdateDTO;
import com.erp.service.AlternativeMaterialService;
import com.erp.vo.AlternativeMaterialVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 替代料管理控制器
 */
@Tag(name = "替代料管理")
@RestController
@RequestMapping("/api/erp/alternative-materials")
@RequiredArgsConstructor
public class AlternativeMaterialController {

    private final AlternativeMaterialService alternativeMaterialService;

    @Operation(summary = "分页查询替代料列表")
    @GetMapping
    public R<PageResult<AlternativeMaterialVO>> pageAlternativeMaterials(AlternativeMaterialQueryDTO queryDTO) {
        return R.success(alternativeMaterialService.pageAlternativeMaterials(queryDTO));
    }

    @Operation(summary = "根据ID查询替代料")
    @GetMapping("/{id}")
    public R<AlternativeMaterialVO> getAlternativeMaterial(@PathVariable Long id) {
        return R.success(alternativeMaterialService.getAlternativeMaterialById(id));
    }

    @Operation(summary = "获取主料的替代料列表")
    @GetMapping("/by-main")
    public R<List<AlternativeMaterialVO>> getAlternativeMaterialsByMainMaterial(
            @RequestParam Long mainMaterialId, 
            @RequestParam Integer mainMaterialType) {
        return R.success(alternativeMaterialService.getAlternativeMaterialsByMainMaterial(mainMaterialId, mainMaterialType));
    }

    @OperationLog(module = "替代料管理", value = "新增替代料")
    @Operation(summary = "创建替代料")
    @PostMapping
    public R<Void> createAlternativeMaterial(@Valid @RequestBody AlternativeMaterialCreateDTO createDTO) {
        alternativeMaterialService.createAlternativeMaterial(createDTO);
        return R.success();
    }

    @OperationLog(module = "替代料管理", value = "编辑替代料")
    @Operation(summary = "更新替代料")
    @PutMapping("/{id}")
    public R<Void> updateAlternativeMaterial(@PathVariable Long id, @Valid @RequestBody AlternativeMaterialUpdateDTO updateDTO) {
        updateDTO.setId(id);
        alternativeMaterialService.updateAlternativeMaterial(updateDTO);
        return R.success();
    }

    @OperationLog(module = "替代料管理", value = "删除替代料")
    @Operation(summary = "删除替代料")
    @DeleteMapping("/{id}")
    public R<Void> deleteAlternativeMaterial(@PathVariable Long id) {
        alternativeMaterialService.deleteAlternativeMaterial(id);
        return R.success();
    }

    @OperationLog(module = "替代料管理", value = "更新替代料状态")
    @Operation(summary = "更新替代料状态")
    @PutMapping("/{id}/status")
    public R<Void> updateAlternativeMaterialStatus(@PathVariable Long id, @RequestBody StatusDTO statusDTO) {
        alternativeMaterialService.updateAlternativeMaterialStatus(id, statusDTO.getStatus());
        return R.success();
    }

    @lombok.Data
    public static class StatusDTO {
        private Integer status;
    }
}
