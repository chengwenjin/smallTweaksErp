package com.erp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("erp_inventory")
public class ErpInventory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long productId;

    private String productCode;

    private String productName;

    private String specification;

    private String unit;

    private String warehouseCode;

    private String warehouseName;

    private String locationCode;

    private BigDecimal quantity;

    private BigDecimal lockedQuantity;

    private BigDecimal availableQuantity;

    private BigDecimal safetyStock;

    private String batchNo;

    private LocalDateTime productionDate;

    private LocalDateTime expiryDate;

    private String remark;

    private Integer status;

    @TableLogic
    private Integer isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private String ext1;

    private String ext2;

    private String ext3;
}
