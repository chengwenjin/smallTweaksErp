package com.erp.dto;

import lombok.Data;

@Data
public class FqcInspectionDisposalDTO {
    private Long id;
    private String inspectionResult;
    private String disposalType;
    private String disposalRemark;
}
