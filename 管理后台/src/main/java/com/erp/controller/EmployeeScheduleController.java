package com.erp.controller;

import com.baserbac.annotation.OperationLog;
import com.baserbac.common.result.R;
import com.baserbac.common.result.PageResult;
import com.erp.dto.EmployeeScheduleCreateDTO;
import com.erp.dto.EmployeeScheduleQueryDTO;
import com.erp.dto.EmployeeScheduleUpdateDTO;
import com.erp.service.EmployeeScheduleService;
import com.erp.vo.EmployeeScheduleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "人员排班管理")
@RestController
@RequestMapping("/api/erp/employee-schedules")
@RequiredArgsConstructor
public class EmployeeScheduleController {

    private final EmployeeScheduleService employeeScheduleService;

    @Operation(summary = "分页查询排班列表")
    @GetMapping
    public R<PageResult<EmployeeScheduleVO>> pageEmployeeSchedules(EmployeeScheduleQueryDTO queryDTO) {
        return R.success(employeeScheduleService.pageEmployeeSchedules(queryDTO));
    }

    @Operation(summary = "根据ID查询排班")
    @GetMapping("/{id}")
    public R<EmployeeScheduleVO> getEmployeeScheduleById(@PathVariable Long id) {
        return R.success(employeeScheduleService.getEmployeeScheduleById(id));
    }

    @OperationLog("创建排班")
    @Operation(summary = "创建排班")
    @PostMapping
    public R<Void> createEmployeeSchedule(@RequestBody EmployeeScheduleCreateDTO createDTO) {
        employeeScheduleService.createEmployeeSchedule(createDTO);
        return R.success();
    }

    @OperationLog("更新排班")
    @Operation(summary = "更新排班")
    @PutMapping("/{id}")
    public R<Void> updateEmployeeSchedule(@PathVariable Long id, @RequestBody EmployeeScheduleUpdateDTO updateDTO) {
        updateDTO.setId(id);
        employeeScheduleService.updateEmployeeSchedule(updateDTO);
        return R.success();
    }

    @OperationLog("删除排班")
    @Operation(summary = "删除排班")
    @DeleteMapping("/{id}")
    public R<Void> deleteEmployeeSchedule(@PathVariable Long id) {
        employeeScheduleService.deleteEmployeeSchedule(id);
        return R.success();
    }

    @OperationLog("更新排班状态")
    @Operation(summary = "更新排班状态")
    @PutMapping("/{id}/status")
    public R<Void> updateEmployeeScheduleStatus(@PathVariable Long id, @RequestParam Integer status) {
        employeeScheduleService.updateEmployeeScheduleStatus(id, status);
        return R.success();
    }

    @OperationLog("批量更新排班状态")
    @Operation(summary = "批量更新排班状态")
    @PutMapping("/batch/status")
    public R<Void> updateEmployeeScheduleStatusBatch(@RequestBody BatchStatusDTO dto) {
        if (dto.getIds() != null) {
            for (Long id : dto.getIds()) {
                employeeScheduleService.updateEmployeeScheduleStatus(id, dto.getStatus());
            }
        }
        return R.success();
    }

    @OperationLog("批量删除排班")
    @Operation(summary = "批量删除排班")
    @DeleteMapping("/batch")
    public R<Void> deleteEmployeeScheduleBatch(@RequestBody BatchDeleteDTO dto) {
        if (dto.getIds() != null) {
            for (Long id : dto.getIds()) {
                employeeScheduleService.deleteEmployeeSchedule(id);
            }
        }
        return R.success();
    }

    @lombok.Data
    public static class BatchStatusDTO {
        private List<Long> ids;
        private Integer status;
    }

    @lombok.Data
    public static class BatchDeleteDTO {
        private List<Long> ids;
    }
}
