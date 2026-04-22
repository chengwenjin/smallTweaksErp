package com.erp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * BOM层级结构VO
 */
@Data
@Schema(description = "BOM层级结构")
public class BomTreeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "节点ID")
    private Long id;

    @Schema(description = "类型：1产品 2物料 3BOM")
    private Integer type;

    @Schema(description = "编码")
    private String code;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "用量")
    private BigDecimal quantity;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "子节点")
    private List<BomTreeVO> children;
}
