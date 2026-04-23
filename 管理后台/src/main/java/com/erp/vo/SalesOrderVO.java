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
@Schema(description = "销售订单信息")
public class SalesOrderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "客户编码")
    private String customerCode;

    @Schema(description = "订单类型：1普通订单 2紧急订单")
    private Integer orderType;

    @Schema(description = "订单类型名称")
    private String orderTypeName;

    @Schema(description = "订单日期")
    private LocalDate orderDate;

    @Schema(description = "交货日期")
    private LocalDate deliveryDate;

    @Schema(description = "订单总金额")
    private BigDecimal totalAmount;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态：1待确认 2已确认 3已发货 4已完成 5已取消")
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
