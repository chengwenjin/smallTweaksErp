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
@Schema(description = "库存信息")
public class InventoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "库存ID")
    private Long id;

    @Schema(description = "产品ID")
    private Long productId;

    @Schema(description = "产品编码")
    private String productCode;

    @Schema(description = "产品名称")
    private String productName;

    @Schema(description = "规格型号")
    private String specification;

    @Schema(description = "计量单位")
    private String unit;

    @Schema(description = "仓库编码")
    private String warehouseCode;

    @Schema(description = "仓库名称")
    private String warehouseName;

    @Schema(description = "库位编码")
    private String locationCode;

    @Schema(description = "库存数量")
    private BigDecimal quantity;

    @Schema(description = "锁定数量")
    private BigDecimal lockedQuantity;

    @Schema(description = "可用数量")
    private BigDecimal availableQuantity;

    @Schema(description = "安全库存")
    private BigDecimal safetyStock;

    @Schema(description = "批次号")
    private String batchNo;

    @Schema(description = "生产日期")
    private LocalDateTime productionDate;

    @Schema(description = "有效期至")
    private LocalDateTime expiryDate;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态：1正常 2冻结")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "创建人")
    private String createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新人")
    private String updateBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
