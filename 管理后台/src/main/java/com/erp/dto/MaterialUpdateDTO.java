package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 物料更新DTO
 */
@Data
@Schema(description = "物料更新参数")
public class MaterialUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "物料ID")
    private Long id;

    @Schema(description = "物料名称")
    private String materialName;

    @Schema(description = "物料类型：1原材料 2半成品 3成品")
    private Integer materialType;

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
}
