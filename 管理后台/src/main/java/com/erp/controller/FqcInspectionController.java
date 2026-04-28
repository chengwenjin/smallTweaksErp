package com.erp.controller;

import com.baserbac.annotation.OperationLog;
import com.baserbac.common.result.R;
import com.baserbac.common.result.PageResult;
import com.erp.dto.FqcInspectionCreateDTO;
import com.erp.dto.FqcInspectionDisposalDTO;
import com.erp.dto.FqcInspectionQueryDTO;
import com.erp.dto.FqcInspectionUpdateDTO;
import com.erp.service.FqcInspectionService;
import com.erp.vo.FqcInspectionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "成品检验管理")
@RestController
@RequestMapping("/api/erp/fqc-inspections")
@RequiredArgsConstructor
public class FqcInspectionController {

    private final FqcInspectionService fqcInspectionService;

    @Operation(summary = "分页查询成品检验列表")
    @GetMapping
    public R<PageResult<FqcInspectionVO>> pageFqcInspections(FqcInspectionQueryDTO queryDTO) {
        return R.success(fqcInspectionService.pageFqcInspections(queryDTO));
    }

    @Operation(summary = "根据ID查询成品检验")
    @GetMapping("/{id}")
    public R<FqcInspectionVO> getFqcInspection(@PathVariable Long id) {
        return R.success(fqcInspectionService.getFqcInspectionById(id));
    }

    @OperationLog(module = "成品检验管理", value = "新增成品检验")
    @Operation(summary = "创建成品检验")
    @PostMapping
    public R<Void> createFqcInspection(@Valid @RequestBody FqcInspectionCreateDTO createDTO) {
        fqcInspectionService.createFqcInspection(createDTO);
        return R.success();
    }

    @OperationLog(module = "成品检验管理", value = "编辑成品检验")
    @Operation(summary = "更新成品检验")
    @PutMapping("/{id}")
    public R<Void> updateFqcInspection(@PathVariable Long id, @Valid @RequestBody FqcInspectionUpdateDTO updateDTO) {
        updateDTO.setId(id);
        fqcInspectionService.updateFqcInspection(updateDTO);
        return R.success();
    }

    @OperationLog(module = "成品检验管理", value = "删除成品检验")
    @Operation(summary = "删除成品检验")
    @DeleteMapping("/{id}")
    public R<Void> deleteFqcInspection(@PathVariable Long id) {
        fqcInspectionService.deleteFqcInspection(id);
        return R.success();
    }

    @OperationLog(module = "成品检验管理", value = "提交检验")
    @Operation(summary = "提交检验")
    @PutMapping("/{id}/submit")
    public R<Void> submitInspection(@PathVariable Long id) {
        fqcInspectionService.submitInspection(id);
        return R.success();
    }

    @OperationLog(module = "成品检验管理", value = "完成检验")
    @Operation(summary = "完成检验")
    @PutMapping("/{id}/complete")
    public R<Void> completeInspection(@PathVariable Long id, @Valid @RequestBody FqcInspectionUpdateDTO updateDTO) {
        updateDTO.setId(id);
        fqcInspectionService.completeInspection(updateDTO);
        return R.success();
    }

    @OperationLog(module = "成品检验管理", value = "不合格品处理")
    @Operation(summary = "不合格品处理")
    @PutMapping("/{id}/disposal")
    public R<Void> disposal(@PathVariable Long id, @Valid @RequestBody FqcInspectionDisposalDTO disposalDTO) {
        disposalDTO.setId(id);
        fqcInspectionService.disposal(disposalDTO);
        return R.success();
    }

    @OperationLog(module = "成品检验管理", value = "取消检验")
    @Operation(summary = "取消检验")
    @PutMapping("/{id}/cancel")
    public R<Void> cancel(@PathVariable Long id) {
        fqcInspectionService.cancel(id);
        return R.success();
    }
}
