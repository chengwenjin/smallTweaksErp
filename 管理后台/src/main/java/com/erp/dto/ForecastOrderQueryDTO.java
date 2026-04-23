package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Schema(description = "预测单查询参数")
public class ForecastOrderQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "预测单编号", example = "FC202604001")
    private String forecastNo;

    @Schema(description = "预测单名称", example = "Q2季度产品需求预测")
    private String forecastName;

    @Schema(description = "预测类型", example = "1")
    private Integer forecastType;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "开始日期", example = "2026-04-01")
    private LocalDate startDate;

    @Schema(description = "结束日期", example = "2026-06-30")
    private LocalDate endDate;
}
