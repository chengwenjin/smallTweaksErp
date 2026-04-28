package com.erp.controller;

import com.baserbac.annotation.OperationLog;
import com.baserbac.common.result.R;
import com.baserbac.common.result.PageResult;
import com.erp.dto.IqcInspectionCreateDTO;
import com.erp.dto.IqcInspectionDisposalDTO;
import com.erp.dto.IqcInspectionQueryDTO;
import com.erp.dto.IqcInspectionUpdateDTO;
import com.erp.service.IqcInspectionService;
import com.erp.vo.IqcInspectionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "来料检验管理")
@RestController
@RequestMapping("/api/erp/iqc-inspections")
@RequiredArgsConstructor
public class IqcInspectionController {

    private final IqcInspectionService iqcInspectionService;

    @Operation(summary = "分页查询来料检验列表")
    @GetMapping
    public R<PageResult<IqcInspectionVO>> pageIqcInspections(IqcInspectionQueryDTO queryDTO) {
        return R.success(iqcInspectionService.pageIqcInspections(queryDTO));
    }

    @Operation(summary = "根据ID查询来料检验")
    @GetMapping("/{id}")
    public R<IqcInspectionVO> getIqcInspection(@PathVariable Long id) {
        return R.success(iqcInspectionService.getIqcInspectionById(id));
    }

    @OperationLog(module = "来料检验管理", value = "新增来料检验")
    @Operation(summary = "创建来料检验")
    @PostMapping
    public R<Void> createIqcInspection(@Valid @RequestBody IqcInspectionCreateDTO createDTO) {
        iqcInspectionService.createIqcInspection(createDTO);
        return R.success();
    }

    @OperationLog(module = "来料检验管理", value = "编辑来料检验")
    @Operation(summary = "更新来料检验")
    @PutMapping("/{id}")
    public R<Void> updateIqcInspection(@PathVariable Long id, @Valid @RequestBody IqcInspectionUpdateDTO updateDTO) {
        updateDTO.setId(id);
        iqcInspectionService.updateIqcInspection(updateDTO);
        return R.success();
    }

    @OperationLog(module = "来料检验管理", value = "删除来料检验")
    @Operation(summary = "删除来料检验")
    @DeleteMapping("/{id}")
    public R<Void> deleteIqcInspection(@PathVariable Long id) {
        iqcInspectionService.deleteIqcInspection(id);
        return R.success();
    }

    @OperationLog(module = "来料检验管理", value = "提交检验")
    @Operation(summary = "提交检验")
    @PutMapping("/{id}/submit")
    public R<Void> submitInspection(@PathVariable Long id) {
        iqcInspectionService.submitInspection(id);
        return R.success();
    }

    @OperationLog(module = "来料检验管理", value = "完成检验")
    @Operation(summary = "完成检验")
    @PutMapping("/{id}/complete")
    public R<Void> completeInspection(@PathVariable Long id, @Valid @RequestBody IqcInspectionUpdateDTO updateDTO) {
        updateDTO.setId(id);
        iqcInspectionService.completeInspection(updateDTO);
        return R.success();
    }

    @OperationLog(module = "来料检验管理", value = "不合格品处理")
    @Operation(summary = "不合格品处理")
    @PutMapping("/{id}/disposal")
    public R<Void> disposal(@PathVariable Long id, @Valid @RequestBody IqcInspectionDisposalDTO disposalDTO) {
        disposalDTO.setId(id);
        iqcInspectionService.disposal(disposalDTO);
        return R.success();
    }

    @OperationLog(module = "来料检验管理", value = "取消检验")
    @Operation(summary = "取消检验")
    @PutMapping("/{id}/cancel")
    public R<Void> cancel(@PathVariable Long id) {
        iqcInspectionService.cancel(id);
        return R.success();
    }
}
