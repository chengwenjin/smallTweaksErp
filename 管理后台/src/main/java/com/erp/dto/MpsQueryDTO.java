package com.erp.dto;

import com.baserbac.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Schema(description = "主生产计划查询参数")
public class MpsQueryDTO extends PageQueryDTO {

    private static final long serialVersionUID = 1L;

    @Schema(description = "MPS编号（模糊查询）")
    private String mpsNo;

    @Schema(description = "计划名称（模糊查询）")
    private String planName;

    @Schema(description = "产品编码（模糊查询）")
    private String productCode;

    @Schema(description = "产品名称（模糊查询）")
    private String productName;

    @Schema(description = "设备编码（模糊查询）")
    private String equipmentCode;

    @Schema(description = "设备名称（模糊查询）")
    private String equipmentName;

    @Schema(description = "计划开始日期")
    private LocalDate planStartDate;

    @Schema(description = "计划结束日期")
    private LocalDate planEndDate;

    @Schema(description = "优先级：1高 2中 3低")
    private Integer priority;

    @Schema(description = "状态：1草稿 2已确认 3生产中 4已完成 5已取消")
    private Integer status;
}
