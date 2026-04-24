package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "齐套预警更新参数")
public class KittingAlertUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "预警ID")
    private Long id;

    @Schema(description = "预计到货日期")
    private LocalDate expectedArrivalDate;

    @Schema(description = "供应商编码")
    private String supplierCode;

    @Schema(description = "供应商名称")
    private String supplierName;

    @Schema(description = "采购订单号")
    private String purchaseOrderNo;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态：1待处理 2已跟催 3已解决 4已忽略")
    private Integer status;
}
