package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 替代料创建DTO
 */
@Data
@Schema(description = "替代料创建参数")
public class AlternativeMaterialCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主料ID")
    private Long mainMaterialId;

    @Schema(description = "主料类型：1产品 2物料")
    private Integer mainMaterialType;

    @Schema(description = "替代料ID")
    private Long alternativeMaterialId;

    @Schema(description = "替代料类型：1产品 2物料")
    private Integer alternativeMaterialType;

    @Schema(description = "替代比例")
    private BigDecimal alternativeRatio;

    @Schema(description = "优先级：1-5（1最高）")
    private Integer priority;

    @Schema(description = "适用场景")
    private String applicableScene;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态：1启用 0禁用")
    private Integer status = 1;
}
