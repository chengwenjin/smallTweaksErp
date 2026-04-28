package com.erp.controller;

import com.baserbac.annotation.OperationLog;
import com.baserbac.common.result.R;
import com.baserbac.common.result.PageResult;
import com.erp.dto.IpqcInspectionCreateDTO;
import com.erp.dto.IpqcInspectionDisposalDTO;
import com.erp.dto.IpqcInspectionQueryDTO;
import com.erp.dto.IpqcInspectionUpdateDTO;
import com.erp.service.IpqcInspectionService;
import com.erp.vo.IpqcInspectionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "过程检验管理")
@RestController
@RequestMapping("/api/erp/ipqc-inspections")
@RequiredArgsConstructor
public class IpqcInspectionController {

    private final IpqcInspectionService ipqcInspectionService;

    @Operation(summary = "分页查询过程检验列表")
    @GetMapping
    public R<PageResult<IpqcInspectionVO>> pageIpqcInspections(IpqcInspectionQueryDTO queryDTO) {
        return R.success(ipqcInspectionService.pageIpqcInspections(queryDTO));
    }

    @Operation(summary = "根据ID查询过程检验")
    @GetMapping("/{id}")
    public R<IpqcInspectionVO> getIpqcInspection(@PathVariable Long id) {
        return R.success(ipqcInspectionService.getIpqcInspectionById(id));
    }

    @OperationLog(module = "过程检验管理", value = "新增过程检验")
    @Operation(summary = "创建过程检验")
    @PostMapping
    public R<Void> createIpqcInspection(@Valid @RequestBody IpqcInspectionCreateDTO createDTO) {
        ipqcInspectionService.createIpqcInspection(createDTO);
        return R.success();
    }

    @OperationLog(module = "过程检验管理", value = "编辑过程检验")
    @Operation(summary = "更新过程检验")
    @PutMapping("/{id}")
    public R<Void> updateIpqcInspection(@PathVariable Long id, @Valid @RequestBody IpqcInspectionUpdateDTO updateDTO) {
        updateDTO.setId(id);
        ipqcInspectionService.updateIpqcInspection(updateDTO);
        return R.success();
    }

    @OperationLog(module = "过程检验管理", value = "删除过程检验")
    @Operation(summary = "删除过程检验")
    @DeleteMapping("/{id}")
    public R<Void> deleteIpqcInspection(@PathVariable Long id) {
        ipqcInspectionService.deleteIpqcInspection(id);
        return R.success();
    }

    @OperationLog(module = "过程检验管理", value = "提交检验")
    @Operation(summary = "提交检验")
    @PutMapping("/{id}/submit")
    public R<Void> submitInspection(@PathVariable Long id) {
        ipqcInspectionService.submitInspection(id);
        return R.success();
    }

    @OperationLog(module = "过程检验管理", value = "完成检验")
    @Operation(summary = "完成检验")
    @PutMapping("/{id}/complete")
    public R<Void> completeInspection(@PathVariable Long id, @Valid @RequestBody IpqcInspectionUpdateDTO updateDTO) {
        updateDTO.setId(id);
        ipqcInspectionService.completeInspection(updateDTO);
        return R.success();
    }

    @OperationLog(module = "过程检验管理", value = "不合格品处理")
    @Operation(summary = "不合格品处理")
    @PutMapping("/{id}/disposal")
    public R<Void> disposal(@PathVariable Long id, @Valid @RequestBody IpqcInspectionDisposalDTO disposalDTO) {
        disposalDTO.setId(id);
        ipqcInspectionService.disposal(disposalDTO);
        return R.success();
    }

    @OperationLog(module = "过程检验管理", value = "取消检验")
    @Operation(summary = "取消检验")
    @PutMapping("/{id}/cancel")
    public R<Void> cancel(@PathVariable Long id) {
        ipqcInspectionService.cancel(id);
        return R.success();
    }
}
