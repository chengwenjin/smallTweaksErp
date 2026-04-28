package com.erp.dto;

import lombok.Data;

@Data
public class IqcInspectionQueryDTO {
    private String inspectionNo;
    private String purchaseOrderNo;
    private String supplierName;
    private String materialCode;
    private String materialName;
    private String batchNo;
    private String inspectionResult;
    private Integer status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
