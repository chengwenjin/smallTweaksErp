package com.erp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("erp_demand_source")
public class ErpDemandSource implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String sourceNo;

    private Integer sourceType;

    private Long sourceId;

    private Long productId;

    private String productCode;

    private String productName;

    private String specification;

    private String unit;

    private BigDecimal demandQuantity;

    private LocalDate demandDate;

    private BigDecimal allocatedQuantity;

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
