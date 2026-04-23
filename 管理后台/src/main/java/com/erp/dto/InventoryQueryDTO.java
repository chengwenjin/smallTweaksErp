package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "库存查询参数")
public class InventoryQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "产品编码", example = "PRD001")
    private String productCode;

    @Schema(description = "产品名称", example = "台式计算机")
    private String productName;

    @Schema(description = "仓库编码", example = "WH001")
    private String warehouseCode;

    @Schema(description = "仓库名称", example = "主仓库")
    private String warehouseName;

    @Schema(description = "状态", example = "1")
    private Integer status;
}
