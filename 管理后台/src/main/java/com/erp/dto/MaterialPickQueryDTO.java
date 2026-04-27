package com.erp.dto;

import com.baserbac.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Schema(description = "领料单查询参数")
public class MaterialPickQueryDTO extends PageQueryDTO {

    private static final long serialVersionUID = 1L;

    @Schema(description = "领料单号（模糊查询）")
    private String pickNo;

    @Schema(description = "工单号（模糊查询）")
    private String workOrderNo;

    @Schema(description = "工单名称（模糊查询）")
    private String workOrderName;

    @Schema(description = "产品编码（模糊查询）")
    private String productCode;

    @Schema(description = "产品名称（模糊查询）")
    private String productName;

    @Schema(description = "物料编码（模糊查询）")
    private String materialCode;

    @Schema(description = "物料名称（模糊查询）")
    private String materialName;

    @Schema(description = "领料人（模糊查询）")
    private String pickerName;

    @Schema(description = "状态：1新建 2待审批 3已审批 4已领料 5已超领待审批")
    private Integer status;

    @Schema(description = "计划领料日期起始")
    private LocalDate planPickDateStart;

    @Schema(description = "计划领料日期结束")
    private LocalDate planPickDateEnd;

    @Schema(description = "实际领料日期起始")
    private LocalDate actualPickDateStart;

    @Schema(description = "实际领料日期结束")
    private LocalDate actualPickDateEnd;

    @Schema(description = "批次号（模糊查询）")
    private String batchNo;

    @Schema(description = "仓库编码（模糊查询）")
    private String warehouseCode;

    @Schema(description = "仓库名称（模糊查询）")
    private String warehouseName;
}
