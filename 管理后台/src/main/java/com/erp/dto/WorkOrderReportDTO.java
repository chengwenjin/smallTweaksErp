package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "工单报工参数")
public class WorkOrderReportDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "工单ID")
    private Long workOrderId;

    @Schema(description = "工单编号")
    private String workOrderNo;

    @Schema(description = "报工数量")
    private BigDecimal reportQuantity;

    @Schema(description = "报废数量")
    private BigDecimal scrappedQuantity;

    @Schema(description = "工序名称")
    private String operationName;

    @Schema(description = "工作中心")
    private String workcenter;

    @Schema(description = "班组ID")
    private Long groupId;

    @Schema(description = "班组名称")
    private String groupName;

    @Schema(description = "操作员")
    private String operator;

    @Schema(description = "备注")
    private String remark;
}
