package com.erp.controller;

import com.baserbac.annotation.OperationLog;
import com.baserbac.common.result.PageResult;
import com.baserbac.common.result.R;
import com.erp.dto.MaterialPickCreateDTO;
import com.erp.dto.MaterialPickQueryDTO;
import com.erp.dto.MaterialPickUpdateDTO;
import com.erp.service.MaterialPickService;
import com.erp.vo.MaterialPickVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "领料单管理")
@RestController
@RequestMapping("/api/erp/material-picks")
@RequiredArgsConstructor
public class MaterialPickController {

    private final MaterialPickService materialPickService;

    @Operation(summary = "分页查询领料单列表")
    @GetMapping("/list")
    public R<PageResult<MaterialPickVO>> list(MaterialPickQueryDTO queryDTO) {
        PageResult<MaterialPickVO> result = materialPickService.pageMaterialPicks(queryDTO);
        return R.success(result);
    }

    @Operation(summary = "根据ID查询领料单详情")
    @GetMapping("/{id}")
    public R<MaterialPickVO> getById(@PathVariable Long id) {
        MaterialPickVO vo = materialPickService.getMaterialPickById(id);
        return R.success(vo);
    }

    @OperationLog(module = "领料单管理", value = "新增领料单")
    @Operation(summary = "创建领料单")
    @PostMapping
    public R<Void> create(@RequestBody @Validated MaterialPickCreateDTO createDTO) {
        materialPickService.createMaterialPick(createDTO);
        return R.success();
    }

    @OperationLog(module = "领料单管理", value = "编辑领料单")
    @Operation(summary = "更新领料单")
    @PutMapping
    public R<Void> update(@RequestBody @Validated MaterialPickUpdateDTO updateDTO) {
        materialPickService.updateMaterialPick(updateDTO);
        return R.success();
    }

    @OperationLog(module = "领料单管理", value = "删除领料单")
    @Operation(summary = "删除领料单")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        materialPickService.deleteMaterialPick(id);
        return R.success();
    }

    @OperationLog(module = "领料单管理", value = "提交审批")
    @Operation(summary = "提交审批")
    @PostMapping("/submit-approval/{id}")
    public R<Void> submitApproval(@PathVariable Long id) {
        materialPickService.submitApproval(id);
        return R.success();
    }

    @OperationLog(module = "领料单管理", value = "审批")
    @Operation(summary = "审批领料单")
    @PostMapping("/approval")
    public R<Void> approval(@RequestParam Long id, @RequestParam Boolean approved, 
                           @RequestParam(required = false) String opinion) {
        materialPickService.approval(id, approved, opinion);
        return R.success();
    }

    @OperationLog(module = "领料单管理", value = "领料")
    @Operation(summary = "执行领料")
    @PostMapping("/pick")
    public R<Void> pick(@RequestParam Long id, @RequestParam BigDecimal quantity) {
        materialPickService.pickMaterial(id, quantity);
        return R.success();
    }
}
