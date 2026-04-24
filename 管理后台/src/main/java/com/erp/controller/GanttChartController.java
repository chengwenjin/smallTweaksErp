package com.erp.controller;

import com.baserbac.annotation.OperationLog;
import com.baserbac.common.result.R;
import com.erp.dto.GanttDragDTO;
import com.erp.dto.GanttQueryDTO;
import com.erp.service.GanttChartService;
import com.erp.vo.GanttChartVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "可视化排程-甘特图")
@RestController
@RequestMapping("/api/erp/gantt")
@RequiredArgsConstructor
public class GanttChartController {

    private final GanttChartService ganttChartService;

    @Operation(summary = "获取甘特图数据（按设备分组）")
    @GetMapping("/by-equipment")
    public R<GanttChartVO.ChartDataVO> getGanttByEquipment(GanttQueryDTO queryDTO) {
        return R.success(ganttChartService.getGanttDataByEquipment(queryDTO));
    }

    @Operation(summary = "获取甘特图数据（按产品分组）")
    @GetMapping("/by-product")
    public R<GanttChartVO.ChartDataVO> getGanttByProduct(GanttQueryDTO queryDTO) {
        return R.success(ganttChartService.getGanttDataByProduct(queryDTO));
    }

    @Operation(summary = "获取甘特图数据（按优先级分组）")
    @GetMapping("/by-priority")
    public R<GanttChartVO.ChartDataVO> getGanttByPriority(GanttQueryDTO queryDTO) {
        return R.success(ganttChartService.getGanttDataByPriority(queryDTO));
    }

    @OperationLog("拖拽调整计划时间")
    @Operation(summary = "拖拽调整计划时间")
    @PostMapping("/drag")
    public R<Void> dragAdjustPlan(@RequestBody GanttDragDTO dragDTO) {
        ganttChartService.dragAdjustPlan(dragDTO);
        return R.success();
    }

    @OperationLog("重新排程")
    @Operation(summary = "订单变更时重新排程")
    @PostMapping("/reschedule")
    public R<Void> reschedule(@RequestBody GanttDragDTO dragDTO) {
        ganttChartService.reschedule(dragDTO);
        return R.success();
    }

    @OperationLog("批量重新排程")
    @Operation(summary = "批量重新排程所有计划")
    @PostMapping("/reschedule-all")
    public R<Void> rescheduleAll() {
        ganttChartService.rescheduleAll();
        return R.success();
    }

    @OperationLog("生成测试数据")
    @Operation(summary = "生成甘特图测试数据（30条以上）")
    @PostMapping("/generate-test-data")
    public R<Integer> generateTestData() {
        int count = ganttChartService.generateTestData();
        return R.success(count);
    }
}
