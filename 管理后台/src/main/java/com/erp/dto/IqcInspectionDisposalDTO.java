package com.erp.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class IqcInspectionDisposalDTO {
    private Long id;
    private String inspectionResult;
    private String disposalType;
    private String disposalRemark;
}
