package com.erp.dto;

import com.baserbac.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 产品查询DTO
 */
@Data
@Schema(description = "产品查询参数")
public class ProductQueryDTO extends PageQueryDTO {

    private static final long serialVersionUID = 1L;

    @Schema(description = "产品编码（模糊查询）")
    private String productCode;

    @Schema(description = "产品名称（模糊查询）")
    private String productName;

    @Schema(description = "产品分类")
    private String category;

    @Schema(description = "品牌")
    private String brand;

    @Schema(description = "状态：1启用 0禁用")
    private Integer status;
}
