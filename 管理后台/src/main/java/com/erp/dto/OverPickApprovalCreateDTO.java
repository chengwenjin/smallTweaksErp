package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "超领审批单创建参数")
public class OverPickApprovalCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "工单ID")
    private Long workOrderId;

    @Schema(description = "工单号")
    private String workOrderNo;

    @Schema(description = "工单名称")
    private String workOrderName;

    @Schema(description = "领料单ID")
    private Long pickId;

    @Schema(description = "领料单号")
    private String pickNo;

    @Schema(description = "产品ID")
    private Long productId;

    @Schema(description = "产品编码")
    private String productCode;

    @Schema(description = "产品名称")
    private String productName;

    @Schema(description = "物料ID")
    private Long materialId;

    @Schema(description = "物料编码")
    private String materialCode;

    @Schema(description = "物料名称")
    private String materialName;

    @Schema(description = "规格型号")
    private String specification;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "计划领料数量（BOM定额）")
    private BigDecimal planQuantity;

    @Schema(description = "超领数量")
    private BigDecimal overPickQuantity;

    @Schema(description = "超领原因")
    private String overPickReason;

    @Schema(description = "申请人ID")
    private Long applicantId;

    @Schema(description = "申请人名称")
    private String applicantName;

    @Schema(description = "备注")
    private String remark;
}
