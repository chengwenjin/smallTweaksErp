package com.erp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 齐套预警实体
 */
@Data
@TableName("erp_kitting_alert")
public class ErpKittingAlert implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String alertNo;

    private Long mpsId;

    private String mpsNo;

    private Long productId;

    private String productCode;

    private String productName;

    private Long materialId;

    private String materialCode;

    private String materialName;

    private String specification;

    private String unit;

    private BigDecimal requiredQuantity;

    private BigDecimal stockQuantity;

    private BigDecimal allocatedQuantity;

    private BigDecimal shortageQuantity;

    private BigDecimal kittingRate;

    private Integer alertLevel;

    private LocalDate requiredDate;

    private LocalDate expectedArrivalDate;

    private String supplierCode;

    private String supplierName;

    private String purchaseOrderNo;

    private Integer syncedToScrm;

    private LocalDateTime syncedTime;

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
