package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "工单下发参数")
public class WorkOrderIssueDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "工单ID列表")
    private List<Long> ids;

    @Schema(description = "工单ID")
    private Long id;

    @Schema(description = "实际开始日期")
    private String actualStartDate;

    @Schema(description = "备注")
    private String remark;
}
