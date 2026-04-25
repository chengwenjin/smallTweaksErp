package com.erp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("erp_work_order")
public class ErpWorkOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String workOrderNo;

    private String workOrderName;

    private String workOrderType;

    private Long sourceMpsId;

    private String sourceMpsNo;

    private Long productId;

    private String productCode;

    private String productName;

    private String specification;

    private String unit;

    private BigDecimal planQuantity;

    private BigDecimal actualQuantity;

    private BigDecimal completedQuantity;

    private BigDecimal scrappedQuantity;

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

    private String orderSource;

    private Long orderSourceId;

    private String orderSourceNo;

    private LocalDate deliveryDate;

    private BigDecimal completionRate;

    private Long approvalUserId;

    private String approvalUserName;

    private LocalDateTime approvalTime;

    private String approvalOpinion;

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
