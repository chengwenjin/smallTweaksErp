package com.erp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("erp_material_return")
public class ErpMaterialReturn implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String returnNo;

    private Integer returnType;

    private Long workOrderId;

    private String workOrderNo;

    private String workOrderName;

    private Long pickId;

    private String pickNo;

    private Long productId;

    private String productCode;

    private String productName;

    private Long materialId;

    private String materialCode;

    private String materialName;

    private String specification;

    private String unit;

    private BigDecimal returnQuantity;

    private BigDecimal returnValue;

    private String returnReason;

    private LocalDate returnDate;

    private Long warehouseId;

    private String warehouseCode;

    private String warehouseName;

    private Long operatorId;

    private String operatorName;

    private String batchNo;

    private String remark;

    private Integer status;

    private Long approvalUserId;

    private String approvalUserName;

    private LocalDateTime approvalTime;

    private String approvalOpinion;

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
