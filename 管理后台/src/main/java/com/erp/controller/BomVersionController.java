package com.erp.controller;

import com.baserbac.annotation.OperationLog;
import com.baserbac.common.result.R;
import com.baserbac.common.result.PageResult;
import com.erp.dto.BomVersionCreateDTO;
import com.erp.dto.BomVersionQueryDTO;
import com.erp.dto.BomVersionUpdateDTO;
import com.erp.service.BomVersionService;
import com.erp.vo.BomVersionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * BOM版本管理控制器
 */
@Tag(name = "BOM版本管理")
@RestController
@RequestMapping("/api/erp/bom-versions")
@RequiredArgsConstructor
public class BomVersionController {

    private final BomVersionService bomVersionService;

    @Operation(summary = "分页查询BOM版本列表")
    @GetMapping
    public R<PageResult<BomVersionVO>> pageBomVersions(BomVersionQueryDTO queryDTO) {
        return R.success(bomVersionService.pageBomVersions(queryDTO));
    }

    @Operation(summary = "根据ID查询BOM版本")
    @GetMapping("/{id}")
    public R<BomVersionVO> getBomVersion(@PathVariable Long id) {
        return R.success(bomVersionService.getBomVersionById(id));
    }

    @OperationLog(module = "BOM版本管理", value = "新增BOM版本")
    @Operation(summary = "创建BOM版本")
    @PostMapping
    public R<Void> createBomVersion(@Valid @RequestBody BomVersionCreateDTO createDTO) {
        bomVersionService.createBomVersion(createDTO);
        return R.success();
    }

    @OperationLog(module = "BOM版本管理", value = "编辑BOM版本")
    @Operation(summary = "更新BOM版本")
    @PutMapping("/{id}")
    public R<Void> updateBomVersion(@PathVariable Long id, @Valid @RequestBody BomVersionUpdateDTO updateDTO) {
        updateDTO.setId(id);
        bomVersionService.updateBomVersion(updateDTO);
        return R.success();
    }

    @OperationLog(module = "BOM版本管理", value = "删除BOM版本")
    @Operation(summary = "删除BOM版本")
    @DeleteMapping("/{id}")
    public R<Void> deleteBomVersion(@PathVariable Long id) {
        bomVersionService.deleteBomVersion(id);
        return R.success();
    }

    @OperationLog(module = "BOM版本管理", value = "更新BOM版本状态")
    @Operation(summary = "更新BOM版本状态")
    @PutMapping("/{id}/status")
    public R<Void> updateBomVersionStatus(@PathVariable Long id, @RequestBody StatusDTO statusDTO) {
        bomVersionService.updateBomVersionStatus(id, statusDTO.getStatus());
        return R.success();
    }

    @lombok.Data
    public static class StatusDTO {
        private Integer status;
    }
}
