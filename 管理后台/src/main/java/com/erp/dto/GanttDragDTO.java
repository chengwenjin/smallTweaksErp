package com.erp.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GanttDragDTO {

    private Long id;
    
    private LocalDate newStartDate;
    
    private LocalDate newEndDate;
    
    private Long newResourceId;
    
    private String newResourceType;
    
    private Integer newPriority;
    
    private String reason;
}
