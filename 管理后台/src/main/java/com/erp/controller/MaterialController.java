package com.erp.controller;

import com.baserbac.annotation.OperationLog;
import com.baserbac.common.result.R;
import com.baserbac.common.result.PageResult;
import com.erp.dto.MaterialCreateDTO;
import com.erp.dto.MaterialQueryDTO;
import com.erp.dto.MaterialUpdateDTO;
import com.erp.service.MaterialService;
import com.erp.vo.MaterialVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 物料管理控制器
 */
@Tag(name = "物料管理")
@RestController
@RequestMapping("/api/erp/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    @Operation(summary = "分页查询物料列表")
    @GetMapping
    public R<PageResult<MaterialVO>> pageMaterials(MaterialQueryDTO queryDTO) {
        return R.success(materialService.pageMaterials(queryDTO));
    }

    @Operation(summary = "根据ID查询物料")
    @GetMapping("/{id}")
    public R<MaterialVO> getMaterial(@PathVariable Long id) {
        return R.success(materialService.getMaterialById(id));
    }

    @OperationLog(module = "物料管理", value = "新增物料")
    @Operation(summary = "创建物料")
    @PostMapping
    public R<Void> createMaterial(@Valid @RequestBody MaterialCreateDTO createDTO) {
        materialService.createMaterial(createDTO);
        return R.success();
    }

    @OperationLog(module = "物料管理", value = "编辑物料")
    @Operation(summary = "更新物料")
    @PutMapping("/{id}")
    public R<Void> updateMaterial(@PathVariable Long id, @Valid @RequestBody MaterialUpdateDTO updateDTO) {
        updateDTO.setId(id);
        materialService.updateMaterial(updateDTO);
        return R.success();
    }

    @OperationLog(module = "物料管理", value = "删除物料")
    @Operation(summary = "删除物料")
    @DeleteMapping("/{id}")
    public R<Void> deleteMaterial(@PathVariable Long id) {
        materialService.deleteMaterial(id);
        return R.success();
    }

    @OperationLog(module = "物料管理", value = "更新物料状态")
    @Operation(summary = "更新物料状态")
    @PutMapping("/{id}/status")
    public R<Void> updateMaterialStatus(@PathVariable Long id, @RequestBody StatusDTO statusDTO) {
        materialService.updateMaterialStatus(id, statusDTO.getStatus());
        return R.success();
    }

    @lombok.Data
    public static class StatusDTO {
        private Integer status;
    }
}
