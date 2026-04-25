package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "工单完工入库参数")
public class WorkOrderCompleteDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "工单ID")
    private Long workOrderId;

    @Schema(description = "工单编号")
    private String workOrderNo;

    @Schema(description = "入库数量")
    private BigDecimal completeQuantity;

    @Schema(description = "实际结束日期")
    private LocalDate actualEndDate;

    @Schema(description = "仓库编码")
    private String warehouseCode;

    @Schema(description = "仓库名称")
    private String warehouseName;

    @Schema(description = "库位编码")
    private String locationCode;

    @Schema(description = "批次号")
    private String batchNo;

    @Schema(description = "备注")
    private String remark;
}
