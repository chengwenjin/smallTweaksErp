package com.erp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 物料信息VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "物料信息")
public class MaterialVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "物料ID")
    private Long id;

    @Schema(description = "物料编码")
    private String materialCode;

    @Schema(description = "物料名称")
    private String materialName;

    @Schema(description = "物料类型：1原材料 2半成品 3成品")
    private Integer materialType;

    @Schema(description = "物料类型名称")
    private String materialTypeName;

    @Schema(description = "规格型号")
    private String specification;

    @Schema(description = "计量单位")
    private String unit;

    @Schema(description = "物料分类")
    private String category;

    @Schema(description = "品牌")
    private String brand;

    @Schema(description = "自定义属性（JSON格式）")
    private String customAttributes;

    @Schema(description = "描述")
    private String description;

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
