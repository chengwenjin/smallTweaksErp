package com.erp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("erp_fqc_inspection")
public class ErpFqcInspection implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String inspectionNo;

    private Long workOrderId;

    private String workOrderNo;

    private Long productId;

    private String productCode;

    private String productName;

    private String specification;

    private String unit;

    private String batchNo;

    private BigDecimal finishedQuantity;

    private BigDecimal inspectionQuantity;

    private String inspectionType;

    private String inspectionStandard;

    private LocalDateTime inspectionDate;

    private Long inspectorId;

    private String inspectorName;

    private Integer qualifiedCount;

    private Integer unqualifiedCount;

    private Integer reworkCount;

    private Integer scrappedCount;

    private BigDecimal qualifiedRate;

    private String inspectionResult;

    private String certificateNo;

    private LocalDateTime certificateDate;

    private String disposalType;

    private String disposalRemark;

    private LocalDateTime disposalDate;

    private Long disposalUserId;

    private String disposalUserName;

    private Integer allowWarehousing;

    private Long warehouseOrderId;

    private String warehouseOrderNo;

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
