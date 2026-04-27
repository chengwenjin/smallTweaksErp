package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "工序报工更新参数")
public class ProcessReportUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "报工记录ID")
    private Long id;

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

    @Schema(description = "工时（小时）")
    private BigDecimal workHours;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "状态：1待报工 2已报工 3已审核")
    private Integer status;

    @Schema(description = "批次号")
    private String batchNo;

    @Schema(description = "备注")
    private String remark;
}
