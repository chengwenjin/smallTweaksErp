package com.erp.dto;

import com.baserbac.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * BOM查询DTO
 */
@Data
@Schema(description = "BOM查询参数")
public class BomQueryDTO extends PageQueryDTO {

    private static final long serialVersionUID = 1L;

    @Schema(description = "父件ID")
    private Long parentId;

    @Schema(description = "父件类型：1产品 2物料")
    private Integer parentType;

    @Schema(description = "子件ID")
    private Long childId;

    @Schema(description = "子件类型：1产品 2物料")
    private Integer childType;

    @Schema(description = "状态：1启用 0禁用")
    private Integer status;
}
