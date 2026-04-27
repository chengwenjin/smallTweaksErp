package com.erp.dto;

import com.baserbac.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Schema(description = "退补料单查询参数")
public class MaterialReturnQueryDTO extends PageQueryDTO {

    private static final long serialVersionUID = 1L;

    @Schema(description = "退补料单号（模糊查询）")
    private String returnNo;

    @Schema(description = "退补料类型：1余料退回 2不良品补料")
    private Integer returnType;

    @Schema(description = "工单号（模糊查询）")
    private String workOrderNo;

    @Schema(description = "工单名称（模糊查询）")
    private String workOrderName;

    @Schema(description = "领料单号（模糊查询）")
    private String pickNo;

    @Schema(description = "产品编码（模糊查询）")
    private String productCode;

    @Schema(description = "产品名称（模糊查询）")
    private String productName;

    @Schema(description = "物料编码（模糊查询）")
    private String materialCode;

    @Schema(description = "物料名称（模糊查询）")
    private String materialName;

    @Schema(description = "操作人员（模糊查询）")
    private String operatorName;

    @Schema(description = "状态：1新建 2待审批 3已审批 4已完成")
    private Integer status;

    @Schema(description = "退补料日期起始")
    private LocalDate returnDateStart;

    @Schema(description = "退补料日期结束")
    private LocalDate returnDateEnd;

    @Schema(description = "批次号（模糊查询）")
    private String batchNo;

    @Schema(description = "仓库编码（模糊查询）")
    private String warehouseCode;

    @Schema(description = "仓库名称（模糊查询）")
    private String warehouseName;
}
