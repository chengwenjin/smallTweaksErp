package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "超领审批单更新参数")
public class OverPickApprovalUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "审批单ID")
    private Long id;

    @Schema(description = "超领数量")
    private BigDecimal overPickQuantity;

    @Schema(description = "超领原因")
    private String overPickReason;

    @Schema(description = "状态：1草稿 2待审批 3已通过 4已驳回 5已撤销")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
