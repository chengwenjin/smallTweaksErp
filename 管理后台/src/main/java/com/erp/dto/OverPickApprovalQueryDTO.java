package com.erp.dto;

import com.baserbac.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "超领审批单查询参数")
public class OverPickApprovalQueryDTO extends PageQueryDTO {

    private static final long serialVersionUID = 1L;

    @Schema(description = "审批单号（模糊查询）")
    private String approvalNo;

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

    @Schema(description = "申请人（模糊查询）")
    private String applicantName;

    @Schema(description = "审批人（模糊查询）")
    private String approvalUserName;

    @Schema(description = "状态：1草稿 2待审批 3已通过 4已驳回 5已撤销")
    private Integer status;

    @Schema(description = "申请日期起始")
    private LocalDate applicationDateStart;

    @Schema(description = "申请日期结束")
    private LocalDate applicationDateEnd;

    @Schema(description = "审批日期起始")
    private LocalDate approvalDateStart;

    @Schema(description = "审批日期结束")
    private LocalDate approvalDateEnd;
}
