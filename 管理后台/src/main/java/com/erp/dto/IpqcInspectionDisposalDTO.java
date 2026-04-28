package com.erp.dto;

import lombok.Data;

@Data
public class IpqcInspectionDisposalDTO {
    private Long id;
    private String inspectionResult;
    private String nextProcessStatus;
    private String disposalType;
    private String disposalRemark;
}
