package com.erp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 替代料信息VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "替代料信息")
public class AlternativeMaterialVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "替代料ID")
    private Long id;

    @Schema(description = "主料ID")
    private Long mainMaterialId;

    @Schema(description = "主料类型：1产品 2物料")
    private Integer mainMaterialType;

    @Schema(description = "主料类型名称")
    private String mainMaterialTypeName;

    @Schema(description = "主料编码")
    private String mainMaterialCode;

    @Schema(description = "主料名称")
    private String mainMaterialName;

    @Schema(description = "替代料ID")
    private Long alternativeMaterialId;

    @Schema(description = "替代料类型：1产品 2物料")
    private Integer alternativeMaterialType;

    @Schema(description = "替代料类型名称")
    private String alternativeMaterialTypeName;

    @Schema(description = "替代料编码")
    private String alternativeMaterialCode;

    @Schema(description = "替代料名称")
    private String alternativeMaterialName;

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

    @Schema(description = "创建人")
    private String createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新人")
    private String updateBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
