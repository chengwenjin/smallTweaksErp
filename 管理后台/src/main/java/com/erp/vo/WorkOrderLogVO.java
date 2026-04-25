package com.erp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "工单流程日志信息")
public class WorkOrderLogVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "日志ID")
    private Long id;

    @Schema(description = "工单ID")
    private Long workOrderId;

    @Schema(description = "工单编号")
    private String workOrderNo;

    @Schema(description = "原状态")
    private Integer fromStatus;

    @Schema(description = "原状态名称")
    private String fromStatusName;

    @Schema(description = "目标状态")
    private Integer toStatus;

    @Schema(description = "目标状态名称")
    private String toStatusName;

    @Schema(description = "操作类型")
    private String operationType;

    @Schema(description = "操作名称")
    private String operationName;

    @Schema(description = "操作数量")
    private BigDecimal operationQuantity;

    @Schema(description = "操作员")
    private String operator;

    @Schema(description = "操作员名称")
    private String operatorName;

    @Schema(description = "操作时间")
    private LocalDateTime operationTime;

    @Schema(description = "操作备注")
    private String operationRemark;
}
