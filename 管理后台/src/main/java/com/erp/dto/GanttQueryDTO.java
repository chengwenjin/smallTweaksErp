package com.erp.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GanttQueryDTO {

    private String groupBy;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private Integer status;
    
    private Integer priority;
    
    private String equipmentCode;
    
    private String productCode;
    
    private String mpsNo;
}
