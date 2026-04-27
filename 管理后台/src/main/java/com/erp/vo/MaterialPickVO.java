package com.erp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "领料单信息")
public class MaterialPickVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "领料单ID")
    private Long id;

    @Schema(description = "领料单号")
    private String pickNo;

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

    @Schema(description = "已领料数量")
    private BigDecimal pickedQuantity;

    @Schema(description = "剩余领料数量")
    private BigDecimal remainingQuantity;

    @Schema(description = "计划领料日期")
    private LocalDate planPickDate;

    @Schema(description = "实际领料日期")
    private LocalDate actualPickDate;

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

    @Schema(description = "状态：1新建 2待审批 3已审批 4已领料 5已超领待审批")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "是否超领")
    private Boolean isOverPick;

    @Schema(description = "超领数量")
    private BigDecimal overPickQuantity;

    @Schema(description = "创建人")
    private String createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新人")
    private String updateBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
