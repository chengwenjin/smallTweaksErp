package com.erp.dto;

import com.baserbac.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "工序报工查询参数")
public class ProcessReportQueryDTO extends PageQueryDTO {

    private static final long serialVersionUID = 1L;

    @Schema(description = "报工单号（模糊查询）")
    private String reportNo;

    @Schema(description = "工单号（模糊查询）")
    private String workOrderNo;

    @Schema(description = "工单名称（模糊查询）")
    private String workOrderName;

    @Schema(description = "工序编码（模糊查询）")
    private String processCode;

    @Schema(description = "工序名称（模糊查询）")
    private String processName;

    @Schema(description = "设备编码（模糊查询）")
    private String equipmentCode;

    @Schema(description = "设备名称（模糊查询）")
    private String equipmentName;

    @Schema(description = "操作人员（模糊查询）")
    private String operatorName;

    @Schema(description = "状态：1待报工 2已报工 3已审核")
    private Integer status;

    @Schema(description = "报工日期起始")
    private LocalDate reportDateStart;

    @Schema(description = "报工日期结束")
    private LocalDate reportDateEnd;

    @Schema(description = "条码（模糊查询）")
    private String barcode;

    @Schema(description = "批次号（模糊查询）")
    private String batchNo;
}
