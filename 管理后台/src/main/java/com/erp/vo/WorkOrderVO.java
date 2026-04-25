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
@Schema(description = "工单信息")
public class WorkOrderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "工单ID")
    private Long id;

    @Schema(description = "工单编号")
    private String workOrderNo;

    @Schema(description = "工单名称")
    private String workOrderName;

    @Schema(description = "工单类型")
    private String workOrderType;

    @Schema(description = "来源MPS ID")
    private Long sourceMpsId;

    @Schema(description = "来源MPS编号")
    private String sourceMpsNo;

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

    @Schema(description = "计划生产数量")
    private BigDecimal planQuantity;

    @Schema(description = "实际生产数量")
    private BigDecimal actualQuantity;

    @Schema(description = "已完成数量")
    private BigDecimal completedQuantity;

    @Schema(description = "报废数量")
    private BigDecimal scrappedQuantity;

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

    @Schema(description = "订单来源")
    private String orderSource;

    @Schema(description = "订单来源ID")
    private Long orderSourceId;

    @Schema(description = "订单来源编号")
    private String orderSourceNo;

    @Schema(description = "交货日期")
    private LocalDate deliveryDate;

    @Schema(description = "完成率")
    private BigDecimal completionRate;

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

    @Schema(description = "状态：1草稿 2待审批 3已审批 4已下发 5领料中 6生产中 7报工中 8待入库 9已完工 10已取消")
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
