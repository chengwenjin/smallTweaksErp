package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "领料单创建参数")
public class MaterialPickCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "工单ID")
    private Long workOrderId;

    @Schema(description = "工单号")
    private String workOrderNo;

    @Schema(description = "工单名称")
    private String workOrderName;

    @Schema(description = "BOM ID")
    private Long bomId;

    @Schema(description = "BOM版本ID")
    private Long bomVersionId;

    @Schema(description = "BOM版本号")
    private String bomVersionNo;

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

    @Schema(description = "实际领料数量")
    private BigDecimal pickedQuantity;

    @Schema(description = "计划领料日期")
    private LocalDate planPickDate;

    @Schema(description = "仓库ID")
    private Long warehouseId;

    @Schema(description = "仓库编码")
    private String warehouseCode;

    @Schema(description = "仓库名称")
    private String warehouseName;

    @Schema(description = "领料人ID")
    private Long pickerId;

    @Schema(description = "领料人名称")
    private String pickerName;

    @Schema(description = "批次号")
    private String batchNo;

    @Schema(description = "备注")
    private String remark;
}
