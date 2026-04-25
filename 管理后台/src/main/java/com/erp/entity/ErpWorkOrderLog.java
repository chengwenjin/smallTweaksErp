package com.erp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("erp_work_order_log")
public class ErpWorkOrderLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long workOrderId;

    private String workOrderNo;

    private Integer fromStatus;

    private String fromStatusName;

    private Integer toStatus;

    private String toStatusName;

    private String operationType;

    private String operationName;

    private BigDecimal operationQuantity;

    private String operator;

    private String operatorName;

    private LocalDateTime operationTime;

    private String operationRemark;

    private String ext1;

    private String ext2;

    private String ext3;
}
