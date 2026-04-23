package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Schema(description = "销售订单查询参数")
public class SalesOrderQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "订单编号", example = "SO20260423001")
    private String orderNo;

    @Schema(description = "客户名称", example = "ABC科技有限公司")
    private String customerName;

    @Schema(description = "订单类型", example = "1")
    private Integer orderType;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "开始日期", example = "2026-04-01")
    private LocalDate startDate;

    @Schema(description = "结束日期", example = "2026-04-30")
    private LocalDate endDate;
}
