package com.erp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 设备管理实体
 */
@Data
@TableName("erp_equipment")
public class ErpEquipment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String equipmentCode;

    private String equipmentName;

    private String equipmentType;

    private String specification;

    private String brand;

    private String model;

    private BigDecimal capacityPerHour;

    private String capacityUnit;

    private LocalDate purchaseDate;

    private LocalDate warrantyExpiryDate;

    private String workshop;

    private String workcenter;

    private LocalDate maintenanceDate;

    private Integer maintenanceIntervalDays;

    private String responsiblePerson;

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
