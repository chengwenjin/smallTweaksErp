package com.erp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 主生产计划(MPS)实体
 */
@Data
@TableName("erp_mps")
public class ErpMps implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String mpsNo;

    private String planName;

    private Integer planType;

    private Long productId;

    private String productCode;

    private String productName;

    private String specification;

    private String unit;

    private BigDecimal netRequirement;

    private BigDecimal plannedQuantity;

    private LocalDate planStartDate;

    private LocalDate planEndDate;

    private LocalDate actualStartDate;

    private LocalDate actualEndDate;

    private Long equipmentId;

    private String equipmentCode;

    private String equipmentName;

    private Long groupId;

    private String groupCode;

    private String groupName;

    private Integer priority;

    private BigDecimal capacityRequired;

    private BigDecimal capacityAvailable;

    private BigDecimal capacityUtilization;

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
