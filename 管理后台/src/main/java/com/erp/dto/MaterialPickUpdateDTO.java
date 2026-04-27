package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "领料单更新参数")
public class MaterialPickUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "领料单ID")
    private Long id;

    @Schema(description = "实际领料数量")
    private BigDecimal pickedQuantity;

    @Schema(description = "实际领料日期")
    private LocalDate actualPickDate;

    @Schema(description = "仓库ID")
    private Long warehouseId;

    @Schema(description = "仓库编码")
    private String warehouseCode;

    @Schema(description = "仓库名称")
    private String warehouseName;

    @Schema(description = "领料人ID")
    private Long pickerId;

    @Schema(description = "领料人名称")
    private String pickerName;

    @Schema(description = "状态：1新建 2待审批 3已审批 4已领料 5已超领待审批")
    private Integer status;

    @Schema(description = "批次号")
    private String batchNo;

    @Schema(description = "备注")
    private String remark;
}
