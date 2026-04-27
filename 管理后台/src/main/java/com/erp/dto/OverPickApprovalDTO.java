package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "超领审批参数")
public class OverPickApprovalDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "审批单ID")
    private Long id;

    @Schema(description = "审批单ID列表")
    private List<Long> ids;

    @Schema(description = "是否通过")
    private Boolean approved;

    @Schema(description = "审批意见")
    private String approvalOpinion;
}
