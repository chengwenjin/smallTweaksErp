package com.erp.dto;

import lombok.Data;

@Data
public class IpqcInspectionQueryDTO {
    private String inspectionNo;
    private String workOrderNo;
    private String productCode;
    private String productName;
    private String processCode;
    private String processName;
    private String batchNo;
    private String inspectionResult;
    private Integer status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
