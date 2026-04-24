package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "主生产计划更新参数")
public class MpsUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "MPS ID")
    private Long id;

    @Schema(description = "计划名称")
    private String planName;

    @Schema(description = "计划生产数量")
    private BigDecimal plannedQuantity;

    @Schema(description = "计划开始日期")
    private LocalDate planStartDate;

    @Schema(description = "计划结束日期")
    private LocalDate planEndDate;

    @Schema(description = "实际开始日期")
    private LocalDate actualStartDate;

    @Schema(description = "实际结束日期")
    private LocalDate actualEndDate;

    @Schema(description = "设备ID")
    private Long equipmentId;

    @Schema(description = "设备编码")
    private String equipmentCode;

    @Schema(description = "设备名称")
    private String equipmentName;

    @Schema(description = "班组ID")
    private Long groupId;

    @Schema(description = "班组编码")
    private String groupCode;

    @Schema(description = "班组名称")
    private String groupName;

    @Schema(description = "优先级：1高 2中 3低")
    private Integer priority;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态：1草稿 2已确认 3生产中 4已完成 5已取消")
    private Integer status;
}
