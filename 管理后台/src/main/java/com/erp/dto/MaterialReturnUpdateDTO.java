package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "退补料单更新参数")
public class MaterialReturnUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "退补料单ID")
    private Long id;

    @Schema(description = "退补料类型：1余料退回 2不良品补料")
    private Integer returnType;

    @Schema(description = "退补料数量")
    private BigDecimal returnQuantity;

    @Schema(description = "退补料价值")
    private BigDecimal returnValue;

    @Schema(description = "退补料原因")
    private String returnReason;

    @Schema(description = "退补料日期")
    private LocalDate returnDate;

    @Schema(description = "仓库ID")
    private Long warehouseId;

    @Schema(description = "仓库编码")
    private String warehouseCode;

    @Schema(description = "仓库名称")
    private String warehouseName;

    @Schema(description = "操作人员ID")
    private Long operatorId;

    @Schema(description = "操作人员名称")
    private String operatorName;

    @Schema(description = "状态：1新建 2待审批 3已审批 4已完成")
    private Integer status;

    @Schema(description = "批次号")
    private String batchNo;

    @Schema(description = "备注")
    private String remark;
}
