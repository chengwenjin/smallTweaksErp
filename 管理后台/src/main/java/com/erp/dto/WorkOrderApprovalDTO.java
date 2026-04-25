package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "工单审批参数")
public class WorkOrderApprovalDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "工单ID列表")
    private List<Long> ids;

    @Schema(description = "工单ID")
    private Long id;

    @Schema(description = "是否通过：true通过 false驳回")
    private Boolean approved;

    @Schema(description = "审批意见")
    private String approvalOpinion;
}
