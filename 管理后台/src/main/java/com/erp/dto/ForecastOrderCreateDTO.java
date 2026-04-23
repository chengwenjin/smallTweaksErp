package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "预测单创建参数")
public class ForecastOrderCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "预测单编号", example = "FC202604001")
    private String forecastNo;

    @Schema(description = "预测单名称", example = "Q2季度产品需求预测")
    private String forecastName;

    @Schema(description = "预测类型：1月度预测 2季度预测 3年度预测", example = "1")
    private Integer forecastType = 1;

    @Schema(description = "开始日期", example = "2026-04-01")
    private LocalDate startDate;

    @Schema(description = "结束日期", example = "2026-06-30")
    private LocalDate endDate;

    @Schema(description = "总预测数量", example = "1000.00")
    private BigDecimal totalQuantity;

    @Schema(description = "备注", example = "根据历史数据预测")
    private String remark;

    @Schema(description = "状态：1草稿 2已确认 3已完成 4已取消", example = "1")
    private Integer status = 1;
}
