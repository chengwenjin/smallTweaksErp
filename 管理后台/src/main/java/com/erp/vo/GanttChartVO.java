package com.erp.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class GanttChartVO {

    private String resourceType;
    
    private Long resourceId;
    
    private String resourceCode;
    
    private String resourceName;
    
    private String resourceTypeDesc;
    
    private List<TaskVO> tasks;

    @Data
    @Builder
    public static class TaskVO {
        private Long id;
        private String taskNo;
        private String taskName;
        private Long productId;
        private String productCode;
        private String productName;
        private String specification;
        private BigDecimal plannedQuantity;
        private BigDecimal actualQuantity;
        private LocalDate planStartDate;
        private LocalDate planEndDate;
        private LocalDate actualStartDate;
        private LocalDate actualEndDate;
        private Long resourceId;
        private String resourceCode;
        private String resourceName;
        private Integer priority;
        private String priorityName;
        private Integer status;
        private String statusName;
        private Integer progress;
        private String color;
        private String remark;
    }
    
    @Data
    @Builder
    public static class GroupVO {
        private String groupType;
        private String groupTypeName;
        private List<GanttChartVO> resources;
    }
    
    @Data
    @Builder
    public static class ChartDataVO {
        private LocalDate minDate;
        private LocalDate maxDate;
        private List<GroupVO> groups;
        private List<TaskVO> allTasks;
    }
}
