package com.erp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("erp_process_report")
public class ErpProcessReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String reportNo;

    private Long workOrderId;

    private String workOrderNo;

    private String workOrderName;

    private String productName;

    private Long processId;

    private String processCode;

    private String processName;

    private Long equipmentId;

    private String equipmentCode;

    private String equipmentName;

    private Long operatorId;

    private String operatorCode;

    private String operatorName;

    private BigDecimal reportQuantity;

    private BigDecimal qualifiedQuantity;

    private BigDecimal scrappedQuantity;

    private BigDecimal reworkQuantity;

    private String unit;

    private BigDecimal workHours;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String barcode;

    private String batchNo;

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
