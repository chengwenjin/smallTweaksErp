package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "退补料单创建参数")
public class MaterialReturnCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "退补料类型：1余料退回 2不良品补料")
    private Integer returnType;

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

    @Schema(description = "退补料数量")
    private BigDecimal returnQuantity;

    @Schema(description = "退补料价值")
    private BigDecimal returnValue;

    @Schema(description = "退补料原因")
    private String returnReason;

    @Schema(description = "退补料日期")
    private LocalDate returnDate;

    @Schema(description = "仓库ID")
    private Long warehouseId;

    @Schema(description = "仓库编码")
    private String warehouseCode;

    @Schema(description = "仓库名称")
    private String warehouseName;

    @Schema(description = "操作人员ID")
    private Long operatorId;

    @Schema(description = "操作人员名称")
    private String operatorName;

    @Schema(description = "批次号")
    private String batchNo;

    @Schema(description = "备注")
    private String remark;
}
