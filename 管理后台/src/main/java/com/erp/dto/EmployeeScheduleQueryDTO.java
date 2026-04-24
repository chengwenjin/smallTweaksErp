package com.erp.dto;

import com.baserbac.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Schema(description = "人员排班查询参数")
public class EmployeeScheduleQueryDTO extends PageQueryDTO {

    private static final long serialVersionUID = 1L;

    @Schema(description = "排班编号（模糊查询）")
    private String scheduleNo;

    @Schema(description = "班组编码（模糊查询）")
    private String groupCode;

    @Schema(description = "班组名称（模糊查询）")
    private String groupName;

    @Schema(description = "排班日期")
    private LocalDate scheduleDate;

    @Schema(description = "班次类型：1白班 2夜班 3中班")
    private Integer shiftType;

    @Schema(description = "状态：1正常 2取消 3完成")
    private Integer status;
}
