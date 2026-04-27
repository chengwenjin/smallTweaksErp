package com.erp.controller;

import com.baserbac.annotation.OperationLog;
import com.baserbac.common.result.PageResult;
import com.baserbac.common.result.R;
import com.erp.dto.MaterialReturnCreateDTO;
import com.erp.dto.MaterialReturnQueryDTO;
import com.erp.dto.MaterialReturnUpdateDTO;
import com.erp.service.MaterialReturnService;
import com.erp.vo.MaterialReturnVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "退补料管理")
@RestController
@RequestMapping("/api/erp/material-returns")
@RequiredArgsConstructor
public class MaterialReturnController {

    private final MaterialReturnService materialReturnService;

    @Operation(summary = "分页查询退补料单列表")
    @GetMapping("/list")
    public R<PageResult<MaterialReturnVO>> list(MaterialReturnQueryDTO queryDTO) {
        PageResult<MaterialReturnVO> result = materialReturnService.pageMaterialReturns(queryDTO);
        return R.success(result);
    }

    @Operation(summary = "根据ID查询退补料单详情")
    @GetMapping("/{id}")
    public R<MaterialReturnVO> getById(@PathVariable Long id) {
        MaterialReturnVO vo = materialReturnService.getMaterialReturnById(id);
        return R.success(vo);
    }

    @OperationLog(module = "退补料管理", value = "新增退补料单")
    @Operation(summary = "创建退补料单")
    @PostMapping
    public R<Void> create(@RequestBody @Validated MaterialReturnCreateDTO createDTO) {
        materialReturnService.createMaterialReturn(createDTO);
        return R.success();
    }

    @OperationLog(module = "退补料管理", value = "编辑退补料单")
    @Operation(summary = "更新退补料单")
    @PutMapping
    public R<Void> update(@RequestBody @Validated MaterialReturnUpdateDTO updateDTO) {
        materialReturnService.updateMaterialReturn(updateDTO);
        return R.success();
    }

    @OperationLog(module = "退补料管理", value = "删除退补料单")
    @Operation(summary = "删除退补料单")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        materialReturnService.deleteMaterialReturn(id);
        return R.success();
    }

    @OperationLog(module = "退补料管理", value = "提交审批")
    @Operation(summary = "提交审批")
    @PostMapping("/submit-approval/{id}")
    public R<Void> submitApproval(@PathVariable Long id) {
        materialReturnService.submitApproval(id);
        return R.success();
    }

    @OperationLog(module = "退补料管理", value = "审批")
    @Operation(summary = "审批退补料单")
    @PostMapping("/approval")
    public R<Void> approval(@RequestParam Long id, @RequestParam Boolean approved, 
                           @RequestParam(required = false) String opinion) {
        materialReturnService.approval(id, approved, opinion);
        return R.success();
    }

    @OperationLog(module = "退补料管理", value = "完成")
    @Operation(summary = "完成退补料")
    @PostMapping("/complete/{id}")
    public R<Void> complete(@PathVariable Long id) {
        materialReturnService.complete(id);
        return R.success();
    }
}
