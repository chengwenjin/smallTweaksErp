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
@Schema(description = "超领审批单信息")
public class OverPickApprovalVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "审批单ID")
    private Long id;

    @Schema(description = "审批单号")
    private String approvalNo;

    @Schema(description = "工单ID")
    private Long workOrderId;

    @Schema(description = "工单号")
    private String workOrderNo;

    @Schema(description = "工单名称")
    private String workOrderName;

    @Schema(description = "领料单ID")
    private Long pickId;

    @Schema(description = "领料单号")
    private String pickNo;

    @Schema(description = "产品ID")
    private Long productId;

    @Schema(description = "产品编码")
    private String productCode;

    @Schema(description = "产品名称")
    private String productName;

    @Schema(description = "物料ID")
    private Long materialId;

    @Schema(description = "物料编码")
    private String materialCode;

    @Schema(description = "物料名称")
    private String materialName;

    @Schema(description = "规格型号")
    private String specification;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "计划领料数量（BOM定额）")
    private BigDecimal planQuantity;

    @Schema(description = "超领数量")
    private BigDecimal overPickQuantity;

    @Schema(description = "总领料数量")
    private BigDecimal totalQuantity;

    @Schema(description = "超领原因")
    private String overPickReason;

    @Schema(description = "申请人ID")
    private Long applicantId;

    @Schema(description = "申请人名称")
    private String applicantName;

    @Schema(description = "申请时间")
    private LocalDateTime applicationTime;

    @Schema(description = "审批用户ID")
    private Long approvalUserId;

    @Schema(description = "审批用户名")
    private String approvalUserName;

    @Schema(description = "审批时间")
    private LocalDateTime approvalTime;

    @Schema(description = "审批意见")
    private String approvalOpinion;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态：1草稿 2待审批 3已通过 4已驳回 5已撤销")
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
