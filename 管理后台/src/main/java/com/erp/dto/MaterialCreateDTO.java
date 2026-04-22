package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 物料创建DTO
 */
@Data
@Schema(description = "物料创建参数")
public class MaterialCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "物料编码不能为空")
    @Schema(description = "物料编码", example = "MAT001")
    private String materialCode;

    @NotBlank(message = "物料名称不能为空")
    @Schema(description = "物料名称", example = "钢材")
    private String materialName;

    @NotNull(message = "物料类型不能为空")
    @Schema(description = "物料类型：1原材料 2半成品 3成品", example = "1")
    private Integer materialType;

    @Schema(description = "规格型号", example = "Q235 10mm")
    private String specification;

    @Schema(description = "计量单位", example = "吨")
    private String unit;

    @Schema(description = "物料分类", example = "金属材料")
    private String category;

    @Schema(description = "品牌", example = "宝钢")
    private String brand;

    @Schema(description = "自定义属性（JSON格式）", example = "{\"颜色\":\"黑色\",\"材质\":\"碳钢\"}")
    private String customAttributes;

    @Schema(description = "描述", example = "优质碳素结构钢")
    private String description;

    @Schema(description = "状态：1启用 0禁用", example = "1")
    private Integer status = 1;
}
