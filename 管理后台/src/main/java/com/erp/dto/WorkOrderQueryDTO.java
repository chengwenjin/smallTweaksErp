package com.erp.dto;

import com.baserbac.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Schema(description = "工单查询参数")
public class WorkOrderQueryDTO extends PageQueryDTO {

    private static final long serialVersionUID = 1L;

    @Schema(description = "工单编号（模糊查询）")
    private String workOrderNo;

    @Schema(description = "工单名称（模糊查询）")
    private String workOrderName;

    @Schema(description = "工单类型")
    private String workOrderType;

    @Schema(description = "产品编码（模糊查询）")
    private String productCode;

    @Schema(description = "产品名称（模糊查询）")
    private String productName;

    @Schema(description = "设备编码（模糊查询）")
    private String equipmentCode;

    @Schema(description = "设备名称（模糊查询）")
    private String equipmentName;

    @Schema(description = "班组编码（模糊查询）")
    private String groupCode;

    @Schema(description = "班组名称（模糊查询）")
    private String groupName;

    @Schema(description = "优先级：1高 2中 3低")
    private Integer priority;

    @Schema(description = "状态：1草稿 2待审批 3已审批 4已下发 5领料中 6生产中 7报工中 8待入库 9已完工 10已取消")
    private Integer status;

    @Schema(description = "计划开始日期起始")
    private LocalDate planStartDateStart;

    @Schema(description = "计划开始日期结束")
    private LocalDate planStartDateEnd;

    @Schema(description = "计划结束日期起始")
    private LocalDate planEndDateStart;

    @Schema(description = "计划结束日期结束")
    private LocalDate planEndDateEnd;

    @Schema(description = "交货日期起始")
    private LocalDate deliveryDateStart;

    @Schema(description = "交货日期结束")
    private LocalDate deliveryDateEnd;
}
