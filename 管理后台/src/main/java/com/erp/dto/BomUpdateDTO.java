package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * BOM更新DTO
 */
@Data
@Schema(description = "BOM更新参数")
public class BomUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "BOM ID")
    private Long id;

    @Schema(description = "用量")
    private BigDecimal quantity;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态：1启用 0禁用")
    private Integer status;
}
