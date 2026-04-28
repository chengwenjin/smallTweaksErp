package com.erp.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class IqcInspectionUpdateDTO {
    private Long id;
    private String inspectionNo;
    private Long supplierId;
    private String supplierCode;
    private String supplierName;
    private Long materialId;
    private String materialCode;
    private String materialName;
    private String specification;
    private String unit;
    private String batchNo;
    private BigDecimal receivedQuantity;
    private BigDecimal sampleQuantity;
    private String inspectionType;
    private String inspectionStandard;
    private Long inspectorId;
    private String inspectorName;
    private Integer qualifiedCount;
    private Integer unqualifiedCount;
    private BigDecimal qualifiedRate;
    private String inspectionResult;
    private String disposalType;
    private String disposalRemark;
    private Long disposalUserId;
    private String disposalUserName;
    private Integer status;
    private String remark;
}
