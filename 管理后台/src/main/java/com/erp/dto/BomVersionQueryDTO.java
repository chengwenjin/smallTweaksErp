package com.erp.dto;

import com.baserbac.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * BOM版本查询DTO
 */
@Data
@Schema(description = "BOM版本查询参数")
public class BomVersionQueryDTO extends PageQueryDTO {

    private static final long serialVersionUID = 1L;

    @Schema(description = "BOM ID")
    private Long bomId;

    @Schema(description = "版本号")
    private String version;

    @Schema(description = "ECN编号")
    private String ecnNumber;

    @Schema(description = "状态：1启用 0禁用")
    private Integer status;
}
