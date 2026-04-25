package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "工单领料参数")
public class WorkOrderPickDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "工单ID")
    private Long workOrderId;

    @Schema(description = "工单编号")
    private String workOrderNo;

    @Schema(description = "领料明细列表")
    private List<PickDetailDTO> details;

    @Schema(description = "备注")
    private String remark;

    @Data
    public static class PickDetailDTO implements Serializable {
        private Long materialId;
        private String materialCode;
        private String materialName;
        private String specification;
        private String unit;
        private BigDecimal pickQuantity;
        private String warehouseCode;
        private String warehouseName;
        private String locationCode;
        private String batchNo;
    }
}
