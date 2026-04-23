package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "净需求查询参数")
public class NetRequirementQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "计划编号", example = "NR202604001")
    private String planNo;

    @Schema(description = "产品编码", example = "PRD001")
    private String productCode;

    @Schema(description = "产品名称", example = "台式计算机")
    private String productName;

    @Schema(description = "状态：1待确认 2已确认 3已执行", example = "1")
    private Integer status;
}
