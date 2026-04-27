package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "扫码报工参数")
public class ProcessReportScanDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "条码内容")
    private String barcode;

    @Schema(description = "操作人员ID")
    private Long operatorId;

    @Schema(description = "操作人员名称")
    private String operatorName;

    @Schema(description = "设备ID")
    private Long equipmentId;

    @Schema(description = "报工数量")
    private java.math.BigDecimal reportQuantity;

    @Schema(description = "报废数量")
    private java.math.BigDecimal scrappedQuantity;

    @Schema(description = "备注")
    private String remark;
}
