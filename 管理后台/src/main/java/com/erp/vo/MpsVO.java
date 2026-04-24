package com.erp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "主生产计划信息")
public class MpsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "MPS ID")
    private Long id;

    @Schema(description = "MPS编号")
    private String mpsNo;

    @Schema(description = "计划名称")
    private String planName;

    @Schema(description = "计划类型：1正式计划 2模拟计划")
    private Integer planType;

    @Schema(description = "计划类型名称")
    private String planTypeName;

    @Schema(description = "产品ID")
    private Long productId;

    @Schema(description = "产品编码")
    private String productCode;

    @Schema(description = "产品名称")
    private String productName;

    @Schema(description = "规格型号")
    private String specification;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "净需求数量")
    private BigDecimal netRequirement;

    @Schema(description = "计划生产数量")
    private BigDecimal plannedQuantity;

    @Schema(description = "计划开始日期")
    private LocalDate planStartDate;

    @Schema(description = "计划结束日期")
    private LocalDate planEndDate;

    @Schema(description = "实际开始日期")
    private LocalDate actualStartDate;

    @Schema(description = "实际结束日期")
    private LocalDate actualEndDate;

    @Schema(description = "设备ID")
    private Long equipmentId;

    @Schema(description = "设备编码")
    private String equipmentCode;

    @Schema(description = "设备名称")
    private String equipmentName;

    @Schema(description = "班组ID")
    private Long groupId;

    @Schema(description = "班组编码")
    private String groupCode;

    @Schema(description = "班组名称")
    private String groupName;

    @Schema(description = "优先级：1高 2中 3低")
    private Integer priority;

    @Schema(description = "优先级名称")
    private String priorityName;

    @Schema(description = "所需产能")
    private BigDecimal capacityRequired;

    @Schema(description = "可用产能")
    private BigDecimal capacityAvailable;

    @Schema(description = "产能利用率")
    private BigDecimal capacityUtilization;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态：1草稿 2已确认 3生产中 4已完成 5已取消")
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
