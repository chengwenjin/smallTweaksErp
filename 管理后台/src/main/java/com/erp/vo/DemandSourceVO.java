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
@Schema(description = "需求来源信息")
public class DemandSourceVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "需求来源ID")
    private Long id;

    @Schema(description = "来源编号")
    private String sourceNo;

    @Schema(description = "来源类型：1销售订单 2预测单")
    private Integer sourceType;

    @Schema(description = "来源类型名称")
    private String sourceTypeName;

    @Schema(description = "来源单据ID")
    private Long sourceId;

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

    @Schema(description = "需求数量")
    private BigDecimal demandQuantity;

    @Schema(description = "需求日期")
    private LocalDate demandDate;

    @Schema(description = "已分配数量")
    private BigDecimal allocatedQuantity;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态：1待处理 2已处理 3已取消")
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
