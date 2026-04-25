package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "工单创建参数")
public class WorkOrderCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "工单名称")
    private String workOrderName;

    @Schema(description = "工单类型")
    private String workOrderType;

    @Schema(description = "来源MPS ID")
    private Long sourceMpsId;

    @Schema(description = "来源MPS编号")
    private String sourceMpsNo;

    @Schema(description = "产品ID")
    private Long productId;

    @Schema(description = "产品编码")
    private String productCode;

    @Schema(description = "产品名称")
    private String productName;

    @Schema(description = "规格型号")
    private String specification;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "计划生产数量")
    private BigDecimal planQuantity;

    @Schema(description = "计划开始日期")
    private LocalDate planStartDate;

    @Schema(description = "计划结束日期")
    private LocalDate planEndDate;

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

    @Schema(description = "订单来源")
    private String orderSource;

    @Schema(description = "订单来源ID")
    private Long orderSourceId;

    @Schema(description = "订单来源编号")
    private String orderSourceNo;

    @Schema(description = "交货日期")
    private LocalDate deliveryDate;

    @Schema(description = "备注")
    private String remark;
}
