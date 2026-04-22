package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * BOM创建DTO
 */
@Data
@Schema(description = "BOM创建参数")
public class BomCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "父件ID")
    private Long parentId;

    @Schema(description = "父件类型：1产品 2物料")
    private Integer parentType;

    @Schema(description = "子件ID")
    private Long childId;

    @Schema(description = "子件类型：1产品 2物料")
    private Integer childType;

    @Schema(description = "用量")
    private BigDecimal quantity;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态：1启用 0禁用")
    private Integer status = 1;
}
