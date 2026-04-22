package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 替代料更新DTO
 */
@Data
@Schema(description = "替代料更新参数")
public class AlternativeMaterialUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "替代料ID")
    private Long id;

    @Schema(description = "替代比例")
    private BigDecimal alternativeRatio;

    @Schema(description = "优先级：1-5（1最高）")
    private Integer priority;

    @Schema(description = "适用场景")
    private String applicableScene;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态：1启用 0禁用")
    private Integer status;
}
