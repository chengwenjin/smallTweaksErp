package com.erp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "工单看板统计信息")
public class WorkOrderDashboardVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "工单总数")
    private Long totalCount;

    @Schema(description = "草稿状态数量")
    private Long draftCount;

    @Schema(description = "待审批状态数量")
    private Long pendingApprovalCount;

    @Schema(description = "已审批状态数量")
    private Long approvedCount;

    @Schema(description = "已下发状态数量")
    private Long issuedCount;

    @Schema(description = "领料中状态数量")
    private Long pickingCount;

    @Schema(description = "生产中状态数量")
    private Long inProductionCount;

    @Schema(description = "报工中状态数量")
    private Long reportingCount;

    @Schema(description = "待入库状态数量")
    private Long pendingStorageCount;

    @Schema(description = "已完工状态数量")
    private Long completedCount;

    @Schema(description = "已取消状态数量")
    private Long cancelledCount;

    @Schema(description = "平均完成率")
    private BigDecimal avgCompletionRate;

    @Schema(description = "按状态统计")
    private List<StatusStatVO> statusStats;

    @Schema(description = "按优先级统计")
    private List<PriorityStatVO> priorityStats;

    @Schema(description = "按班组统计")
    private List<GroupStatVO> groupStats;

    @Schema(description = "按设备统计")
    private List<EquipmentStatVO> equipmentStats;

    @Schema(description = "即将到期的工单列表")
    private List<WorkOrderVO> urgentWorkOrders;

    @Schema(description = "最近创建的工单列表")
    private List<WorkOrderVO> recentWorkOrders;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusStatVO implements Serializable {
        private Integer status;
        private String statusName;
        private Long count;
        private BigDecimal percentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriorityStatVO implements Serializable {
        private Integer priority;
        private String priorityName;
        private Long count;
        private BigDecimal percentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupStatVO implements Serializable {
        private Long groupId;
        private String groupCode;
        private String groupName;
        private Long totalCount;
        private Long completedCount;
        private Long inProductionCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EquipmentStatVO implements Serializable {
        private Long equipmentId;
        private String equipmentCode;
        private String equipmentName;
        private Long totalCount;
        private Long completedCount;
        private Long inProductionCount;
    }
}
