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
@Schema(description = "齐套预警信息")
public class KittingAlertVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "预警ID")
    private Long id;

    @Schema(description = "预警编号")
    private String alertNo;

    @Schema(description = "MPS ID")
    private Long mpsId;

    @Schema(description = "MPS编号")
    private String mpsNo;

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

    @Schema(description = "需求数量")
    private BigDecimal requiredQuantity;

    @Schema(description = "库存数量")
    private BigDecimal stockQuantity;

    @Schema(description = "已分配数量")
    private BigDecimal allocatedQuantity;

    @Schema(description = "缺口数量")
    private BigDecimal shortageQuantity;

    @Schema(description = "齐套率")
    private BigDecimal kittingRate;

    @Schema(description = "预警等级：1紧急 2高 3中 4低")
    private Integer alertLevel;

    @Schema(description = "预警等级名称")
    private String alertLevelName;

    @Schema(description = "需求日期")
    private LocalDate requiredDate;

    @Schema(description = "预计到货日期")
    private LocalDate expectedArrivalDate;

    @Schema(description = "供应商编码")
    private String supplierCode;

    @Schema(description = "供应商名称")
    private String supplierName;

    @Schema(description = "采购订单号")
    private String purchaseOrderNo;

    @Schema(description = "是否已同步SCM：0否 1是")
    private Integer syncedToScrm;

    @Schema(description = "同步时间")
    private LocalDateTime syncedTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态：1待处理 2已跟催 3已解决 4已忽略")
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
