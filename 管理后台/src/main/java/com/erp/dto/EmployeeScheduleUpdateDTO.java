package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Schema(description = "人员排班更新参数")
public class EmployeeScheduleUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "排班ID")
    private Long id;

    @Schema(description = "班组ID")
    private Long groupId;

    @Schema(description = "班组编码")
    private String groupCode;

    @Schema(description = "班组名称")
    private String groupName;

    @Schema(description = "排班日期")
    private LocalDate scheduleDate;

    @Schema(description = "班次类型：1白班 2夜班 3中班")
    private Integer shiftType;

    @Schema(description = "开始时间")
    private LocalTime startTime;

    @Schema(description = "结束时间")
    private LocalTime endTime;

    @Schema(description = "工作时长（小时）")
    private Integer workHours;

    @Schema(description = "员工名单")
    private String employeeNames;

    @Schema(description = "负责人")
    private String responsiblePerson;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态：1正常 2取消 3完成")
    private Integer status;
}
