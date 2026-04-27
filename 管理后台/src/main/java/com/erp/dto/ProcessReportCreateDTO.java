package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "工序报工创建参数")
public class ProcessReportCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "工单ID")
    private Long workOrderId;

    @Schema(description = "工单号")
    private String workOrderNo;

    @Schema(description = "工单名称")
    private String workOrderName;

    @Schema(description = "工序ID")
    private Long processId;

    @Schema(description = "工序编码")
    private String processCode;

    @Schema(description = "工序名称")
    private String processName;

    @Schema(description = "设备ID")
    private Long equipmentId;

    @Schema(description = "设备编码")
    private String equipmentCode;

    @Schema(description = "设备名称")
    private String equipmentName;

    @Schema(description = "操作人员ID")
    private Long operatorId;

    @Schema(description = "操作人员编码")
    private String operatorCode;

    @Schema(description = "操作人员名称")
    private String operatorName;

    @Schema(description = "报工数量")
    private BigDecimal reportQuantity;

    @Schema(description = "合格数量")
    private BigDecimal qualifiedQuantity;

    @Schema(description = "报废数量")
    private BigDecimal scrappedQuantity;

    @Schema(description = "返工数量")
    private BigDecimal reworkQuantity;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "工时（小时）")
    private BigDecimal workHours;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "条码")
    private String barcode;

    @Schema(description = "批次号")
    private String batchNo;

    @Schema(description = "备注")
    private String remark;
}
