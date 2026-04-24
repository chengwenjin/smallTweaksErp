package com.erp.controller;

import com.baserbac.annotation.OperationLog;
import com.baserbac.common.result.R;
import com.baserbac.common.result.PageResult;
import com.erp.dto.WorkGroupCreateDTO;
import com.erp.dto.WorkGroupQueryDTO;
import com.erp.dto.WorkGroupUpdateDTO;
import com.erp.service.WorkGroupService;
import com.erp.vo.WorkGroupVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "班组管理")
@RestController
@RequestMapping("/api/erp/work-groups")
@RequiredArgsConstructor
public class WorkGroupController {

    private final WorkGroupService workGroupService;

    @Operation(summary = "分页查询班组列表")
    @GetMapping
    public R<PageResult<WorkGroupVO>> pageWorkGroups(WorkGroupQueryDTO queryDTO) {
        return R.success(workGroupService.pageWorkGroups(queryDTO));
    }

    @Operation(summary = "根据ID查询班组")
    @GetMapping("/{id}")
    public R<WorkGroupVO> getWorkGroupById(@PathVariable Long id) {
        return R.success(workGroupService.getWorkGroupById(id));
    }

    @OperationLog("创建班组")
    @Operation(summary = "创建班组")
    @PostMapping
    public R<Void> createWorkGroup(@RequestBody WorkGroupCreateDTO createDTO) {
        workGroupService.createWorkGroup(createDTO);
        return R.success();
    }

    @OperationLog("更新班组")
    @Operation(summary = "更新班组")
    @PutMapping("/{id}")
    public R<Void> updateWorkGroup(@PathVariable Long id, @RequestBody WorkGroupUpdateDTO updateDTO) {
        updateDTO.setId(id);
        workGroupService.updateWorkGroup(updateDTO);
        return R.success();
    }

    @OperationLog("删除班组")
    @Operation(summary = "删除班组")
    @DeleteMapping("/{id}")
    public R<Void> deleteWorkGroup(@PathVariable Long id) {
        workGroupService.deleteWorkGroup(id);
        return R.success();
    }

    @OperationLog("更新班组状态")
    @Operation(summary = "更新班组状态")
    @PutMapping("/{id}/status")
    public R<Void> updateWorkGroupStatus(@PathVariable Long id, @RequestParam Integer status) {
        workGroupService.updateWorkGroupStatus(id, status);
        return R.success();
    }

    @OperationLog("批量更新班组状态")
    @Operation(summary = "批量更新班组状态")
    @PutMapping("/batch/status")
    public R<Void> updateWorkGroupStatusBatch(@RequestBody BatchStatusDTO dto) {
        if (dto.getIds() != null) {
            for (Long id : dto.getIds()) {
                workGroupService.updateWorkGroupStatus(id, dto.getStatus());
            }
        }
        return R.success();
    }

    @OperationLog("批量删除班组")
    @Operation(summary = "批量删除班组")
    @DeleteMapping("/batch")
    public R<Void> deleteWorkGroupBatch(@RequestBody BatchDeleteDTO dto) {
        if (dto.getIds() != null) {
            for (Long id : dto.getIds()) {
                workGroupService.deleteWorkGroup(id);
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
