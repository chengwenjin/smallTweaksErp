package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 产品更新DTO
 */
@Data
@Schema(description = "产品更新参数")
public class ProductUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "产品ID")
    private Long id;

    @Schema(description = "产品名称", example = "台式计算机")
    private String productName;

    @Schema(description = "规格型号", example = "ThinkCentre M90a")
    private String specification;

    @Schema(description = "计量单位", example = "台")
    private String unit;

    @Schema(description = "产品分类", example = "计算机设备")
    private String category;

    @Schema(description = "品牌", example = "联想")
    private String brand;

    @Schema(description = "技术参数（JSON格式）", example = "{\"cpu\":\"Intel i5-10400\",\"memory\":\"16GB\"}")
    private String technicalParams;

    @Schema(description = "产品描述", example = "联想台式计算机，商务办公用")
    private String description;

    @Schema(description = "状态：1启用 0禁用", example = "1")
    private Integer status;
}
