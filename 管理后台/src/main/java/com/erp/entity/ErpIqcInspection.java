package com.erp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("erp_iqc_inspection")
public class ErpIqcInspection implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String inspectionNo;

    private Long purchaseOrderId;

    private String purchaseOrderNo;

    private Long supplierId;

    private String supplierCode;

    private String supplierName;

    private Long materialId;

    private String materialCode;

    private String materialName;

    private String specification;

    private String unit;

    private String batchNo;

    private BigDecimal receivedQuantity;

    private BigDecimal sampleQuantity;

    private String inspectionType;

    private String inspectionStandard;

    private LocalDateTime inspectionDate;

    private Long inspectorId;

    private String inspectorName;

    private Integer qualifiedCount;

    private Integer unqualifiedCount;

    private BigDecimal qualifiedRate;

    private String inspectionResult;

    private String disposalType;

    private String disposalRemark;

    private LocalDateTime disposalDate;

    private Long disposalUserId;

    private String disposalUserName;

    private Long workOrderId;

    private String workOrderNo;

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
