package com.erp.dto;

import com.baserbac.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 物料查询DTO
 */
@Data
@Schema(description = "物料查询参数")
public class MaterialQueryDTO extends PageQueryDTO {

    private static final long serialVersionUID = 1L;

    @Schema(description = "物料编码（模糊查询）")
    private String materialCode;

    @Schema(description = "物料名称（模糊查询）")
    private String materialName;

    @Schema(description = "物料类型：1原材料 2半成品 3成品")
    private Integer materialType;

    @Schema(description = "物料分类")
    private String category;

    @Schema(description = "状态：1启用 0禁用")
    private Integer status;
}
