package com.erp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "来料检验信息")
public class IqcInspectionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "检验单号")
    private String inspectionNo;

    @Schema(description = "采购订单ID")
    private Long purchaseOrderId;

    @Schema(description = "采购订单号")
    private String purchaseOrderNo;

    @Schema(description = "供应商ID")
    private Long supplierId;

    @Schema(description = "供应商编码")
    private String supplierCode;

    @Schema(description = "供应商名称")
    private String supplierName;

    @Schema(description = "物料ID")
    private Long materialId;

    @Schema(description = "物料编码")
    private String materialCode;

    @Schema(description = "物料名称")
    private String materialName;

    @Schema(description = "规格型号")
    private String specification;

    @Schema(description = "计量单位")
    private String unit;

    @Schema(description = "批次号")
    private String batchNo;

    @Schema(description = "来料数量")
    private BigDecimal receivedQuantity;

    @Schema(description = "抽样数量")
    private BigDecimal sampleQuantity;

    @Schema(description = "检验类型")
    private String inspectionType;

    @Schema(description = "检验类型名称")
    private String inspectionTypeName;

    @Schema(description = "检验标准")
    private String inspectionStandard;

    @Schema(description = "检验日期")
    private LocalDateTime inspectionDate;

    @Schema(description = "检验员ID")
    private Long inspectorId;

    @Schema(description = "检验员姓名")
    private String inspectorName;

    @Schema(description = "合格数量")
    private Integer qualifiedCount;

    @Schema(description = "不合格数量")
    private Integer unqualifiedCount;

    @Schema(description = "合格率")
    private BigDecimal qualifiedRate;

    @Schema(description = "检验结果")
    private String inspectionResult;

    @Schema(description = "检验结果名称")
    private String inspectionResultName;

    @Schema(description = "处理方式")
    private String disposalType;

    @Schema(description = "处理方式名称")
    private String disposalTypeName;

    @Schema(description = "处理说明")
    private String disposalRemark;

    @Schema(description = "处理日期")
    private LocalDateTime disposalDate;

    @Schema(description = "处理人ID")
    private Long disposalUserId;

    @Schema(description = "处理人姓名")
    private String disposalUserName;

    @Schema(description = "关联工单ID")
    private Long workOrderId;

    @Schema(description = "关联工单号")
    private String workOrderNo;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态")
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
