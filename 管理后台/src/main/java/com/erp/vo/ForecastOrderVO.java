package com.erp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "预测单信息")
public class ForecastOrderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "预测单ID")
    private Long id;

    @Schema(description = "预测单编号")
    private String forecastNo;

    @Schema(description = "预测单名称")
    private String forecastName;

    @Schema(description = "预测类型：1月度预测 2季度预测 3年度预测")
    private Integer forecastType;

    @Schema(description = "预测类型名称")
    private String forecastTypeName;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "总预测数量")
    private BigDecimal totalQuantity;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态：1草稿 2已确认 3已完成 4已取消")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "创建人")
    private String createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新人")
    private String updateBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
