package com.erp.dto;

import com.baserbac.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 替代料查询DTO
 */
@Data
@Schema(description = "替代料查询参数")
public class AlternativeMaterialQueryDTO extends PageQueryDTO {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主料ID")
    private Long mainMaterialId;

    @Schema(description = "主料类型：1产品 2物料")
    private Integer mainMaterialType;

    @Schema(description = "替代料ID")
    private Long alternativeMaterialId;

    @Schema(description = "替代料类型：1产品 2物料")
    private Integer alternativeMaterialType;

    @Schema(description = "状态：1启用 0禁用")
    private Integer status;
}
