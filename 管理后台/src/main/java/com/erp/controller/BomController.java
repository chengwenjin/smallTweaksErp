package com.erp.controller;

import com.baserbac.annotation.OperationLog;
import com.baserbac.common.result.R;
import com.baserbac.common.result.PageResult;
import com.erp.dto.BomCreateDTO;
import com.erp.dto.BomQueryDTO;
import com.erp.dto.BomUpdateDTO;
import com.erp.service.BomService;
import com.erp.vo.BomTreeVO;
import com.erp.vo.BomVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * BOM管理控制器
 */
@Tag(name = "BOM管理")
@RestController
@RequestMapping("/api/erp/boms")
@RequiredArgsConstructor
public class BomController {

    private final BomService bomService;

    @Operation(summary = "分页查询BOM列表")
    @GetMapping
    public R<PageResult<BomVO>> pageBoms(BomQueryDTO queryDTO) {
        return R.success(bomService.pageBoms(queryDTO));
    }

    @Operation(summary = "根据ID查询BOM")
    @GetMapping("/{id}")
    public R<BomVO> getBom(@PathVariable Long id) {
        return R.success(bomService.getBomById(id));
    }

    @Operation(summary = "获取BOM层级结构")
    @GetMapping("/tree")
    public R<BomTreeVO> getBomTree(@RequestParam Long parentId, @RequestParam Integer parentType) {
        return R.success(bomService.getBomTree(parentId, parentType));
    }

    @OperationLog(module = "BOM管理", value = "新增BOM")
    @Operation(summary = "创建BOM")
    @PostMapping
    public R<Void> createBom(@Valid @RequestBody BomCreateDTO createDTO) {
        bomService.createBom(createDTO);
        return R.success();
    }

    @OperationLog(module = "BOM管理", value = "编辑BOM")
    @Operation(summary = "更新BOM")
    @PutMapping("/{id}")
    public R<Void> updateBom(@PathVariable Long id, @Valid @RequestBody BomUpdateDTO updateDTO) {
        updateDTO.setId(id);
        bomService.updateBom(updateDTO);
        return R.success();
    }

    @OperationLog(module = "BOM管理", value = "删除BOM")
    @Operation(summary = "删除BOM")
    @DeleteMapping("/{id}")
    public R<Void> deleteBom(@PathVariable Long id) {
        bomService.deleteBom(id);
        return R.success();
    }

    @OperationLog(module = "BOM管理", value = "更新BOM状态")
    @Operation(summary = "更新BOM状态")
    @PutMapping("/{id}/status")
    public R<Void> updateBomStatus(@PathVariable Long id, @RequestBody StatusDTO statusDTO) {
        bomService.updateBomStatus(id, statusDTO.getStatus());
        return R.success();
    }

    @lombok.Data
    public static class StatusDTO {
        private Integer status;
    }
}
