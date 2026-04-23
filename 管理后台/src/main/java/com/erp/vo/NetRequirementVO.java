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
@Schema(description = "净需求信息")
public class NetRequirementVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "净需求ID")
    private Long id;

    @Schema(description = "计划编号")
    private String planNo;

    @Schema(description = "产品ID")
    private Long productId;

    @Schema(description = "产品编码")
    private String productCode;

    @Schema(description = "产品名称")
    private String productName;

    @Schema(description = "规格型号")
    private String specification;

    @Schema(description = "计量单位")
    private String unit;

    @Schema(description = "需求日期")
    private LocalDate requirementDate;

    @Schema(description = "毛需求")
    private BigDecimal grossDemand;

    @Schema(description = "库存数量")
    private BigDecimal stockQuantity;

    @Schema(description = "锁定数量")
    private BigDecimal lockedQuantity;

    @Schema(description = "安全库存")
    private BigDecimal safetyStock;

    @Schema(description = "可用数量")
    private BigDecimal availableQuantity;

    @Schema(description = "净需求")
    private BigDecimal netRequirement;

    @Schema(description = "计划接收")
    private BigDecimal plannedReceipt;

    @Schema(description = "计划订单")
    private BigDecimal plannedOrder;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态：1待确认 2已确认 3已执行")
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
