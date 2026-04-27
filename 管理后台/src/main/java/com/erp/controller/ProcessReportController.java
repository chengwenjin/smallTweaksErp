package com.erp.controller;

import com.baserbac.annotation.OperationLog;
import com.baserbac.common.result.PageResult;
import com.baserbac.common.result.R;
import com.erp.dto.ProcessReportCreateDTO;
import com.erp.dto.ProcessReportQueryDTO;
import com.erp.dto.ProcessReportScanDTO;
import com.erp.dto.ProcessReportUpdateDTO;
import com.erp.service.ProcessReportService;
import com.erp.vo.ProcessReportVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "工序报工管理")
@RestController
@RequestMapping("/api/erp/process-reports")
@RequiredArgsConstructor
public class ProcessReportController {

    private final ProcessReportService processReportService;

    @Operation(summary = "分页查询工序报工列表")
    @GetMapping("/list")
    public R<PageResult<ProcessReportVO>> list(ProcessReportQueryDTO queryDTO) {
        PageResult<ProcessReportVO> result = processReportService.pageProcessReports(queryDTO);
        return R.success(result);
    }

    @Operation(summary = "根据ID查询工序报工详情")
    @GetMapping("/{id}")
    public R<ProcessReportVO> getById(@PathVariable Long id) {
        ProcessReportVO vo = processReportService.getProcessReportById(id);
        return R.success(vo);
    }

    @OperationLog(module = "工序报工管理", value = "新增工序报工")
    @Operation(summary = "创建工序报工记录")
    @PostMapping
    public R<Void> create(@RequestBody @Validated ProcessReportCreateDTO createDTO) {
        processReportService.createProcessReport(createDTO);
        return R.success();
    }

    @OperationLog(module = "工序报工管理", value = "编辑工序报工")
    @Operation(summary = "更新工序报工记录")
    @PutMapping
    public R<Void> update(@RequestBody @Validated ProcessReportUpdateDTO updateDTO) {
        processReportService.updateProcessReport(updateDTO);
        return R.success();
    }

    @OperationLog(module = "工序报工管理", value = "删除工序报工")
    @Operation(summary = "删除工序报工记录")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        processReportService.deleteProcessReport(id);
        return R.success();
    }

    @OperationLog(module = "工序报工管理", value = "扫码报工")
    @Operation(summary = "扫码报工")
    @PostMapping("/scan")
    public R<ProcessReportVO> scanReport(@RequestBody @Validated ProcessReportScanDTO scanDTO) {
        ProcessReportVO vo = processReportService.scanReport(scanDTO);
        return R.success(vo);
    }
}
