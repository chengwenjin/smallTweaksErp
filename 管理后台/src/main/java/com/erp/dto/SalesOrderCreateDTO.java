package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "销售订单创建参数")
public class SalesOrderCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订单编号", example = "SO20260423001")
    private String orderNo;

    @Schema(description = "客户名称", example = "ABC科技有限公司")
    private String customerName;

    @Schema(description = "客户编码", example = "CUST001")
    private String customerCode;

    @Schema(description = "订单类型：1普通订单 2紧急订单", example = "1")
    private Integer orderType = 1;

    @Schema(description = "订单日期", example = "2026-04-23")
    private LocalDate orderDate;

    @Schema(description = "交货日期", example = "2026-05-01")
    private LocalDate deliveryDate;

    @Schema(description = "订单总金额", example = "10000.00")
    private BigDecimal totalAmount;

    @Schema(description = "备注", example = "急单，请优先处理")
    private String remark;

    @Schema(description = "状态：1待确认 2已确认 3已发货 4已完成 5已取消", example = "1")
    private Integer status = 1;
}
