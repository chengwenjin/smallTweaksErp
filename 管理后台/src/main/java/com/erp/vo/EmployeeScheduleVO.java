package com.erp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "人员排班信息")
public class EmployeeScheduleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "排班ID")
    private Long id;

    @Schema(description = "排班编号")
    private String scheduleNo;

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

    @Schema(description = "班次类型名称")
    private String shiftTypeName;

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

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "创建人")
    private String createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新人")
    private String updateBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
