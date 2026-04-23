package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "库存更新参数")
public class InventoryUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "库存ID", example = "1")
    private Long id;

    @Schema(description = "仓库编码", example = "WH001")
    private String warehouseCode;

    @Schema(description = "仓库名称", example = "主仓库")
    private String warehouseName;

    @Schema(description = "库位编码", example = "A-01-01")
    private String locationCode;

    @Schema(description = "库存数量", example = "100.00")
    private BigDecimal quantity;

    @Schema(description = "锁定数量", example = "10.00")
    private BigDecimal lockedQuantity;

    @Schema(description = "可用数量", example = "90.00")
    private BigDecimal availableQuantity;

    @Schema(description = "安全库存", example = "50.00")
    private BigDecimal safetyStock;

    @Schema(description = "批次号", example = "BATCH20260423001")
    private String batchNo;

    @Schema(description = "生产日期", example = "2026-04-23T10:00:00")
    private LocalDateTime productionDate;

    @Schema(description = "有效期至", example = "2027-04-23T10:00:00")
    private LocalDateTime expiryDate;

    @Schema(description = "备注", example = "新入库")
    private String remark;

    @Schema(description = "状态：1正常 2冻结", example = "1")
    private Integer status;
}
