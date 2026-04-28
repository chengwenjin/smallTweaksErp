package com.erp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("erp_ipqc_inspection")
public class ErpIpqcInspection implements Serializable {

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

    private Long processId;

    private String processCode;

    private String processName;

    private Integer processSequence;

    private Long equipmentId;

    private String equipmentCode;

    private String equipmentName;

    private String batchNo;

    private BigDecimal productionQuantity;

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

    private String nextProcessStatus;

    private String disposalType;

    private String disposalRemark;

    private LocalDateTime disposalDate;

    private Long disposalUserId;

    private String disposalUserName;

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
