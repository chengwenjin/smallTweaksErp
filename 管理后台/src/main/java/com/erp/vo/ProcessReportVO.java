package com.erp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "工序报工记录信息")
public class ProcessReportVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "报工记录ID")
    private Long id;

    @Schema(description = "报工单号")
    private String reportNo;

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

    @Schema(description = "状态：1待报工 2已报工 3已审核")
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
