package com.erp.dto;

import lombok.Data;

@Data
public class FqcInspectionQueryDTO {
    private String inspectionNo;
    private String workOrderNo;
    private String productCode;
    private String productName;
    private String batchNo;
    private String inspectionResult;
    private String certificateNo;
    private Integer allowWarehousing;
    private Integer status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
