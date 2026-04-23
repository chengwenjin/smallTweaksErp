package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "库存创建参数")
public class InventoryCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "产品ID", example = "1")
    private Long productId;

    @Schema(description = "产品编码", example = "PRD001")
    private String productCode;

    @Schema(description = "产品名称", example = "台式计算机")
    private String productName;

    @Schema(description = "规格型号", example = "ThinkCentre M90a")
    private String specification;

    @Schema(description = "计量单位", example = "台")
    private String unit;

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
    private Integer status = 1;
}
