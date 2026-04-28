package com.erp.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FqcInspectionCreateDTO {
    private String inspectionNo;
    private Long workOrderId;
    private String workOrderNo;
    private Long productId;
    private String productCode;
    private String productName;
    private String specification;
    private String unit;
    private String batchNo;
    private BigDecimal finishedQuantity;
    private BigDecimal inspectionQuantity;
    private String inspectionType;
    private String inspectionStandard;
    private Long inspectorId;
    private String inspectorName;
    private Integer status;
    private String remark;
}
