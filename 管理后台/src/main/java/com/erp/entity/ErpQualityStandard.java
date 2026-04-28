package com.erp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("erp_quality_standard")
public class ErpQualityStandard implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String standardNo;

    private String standardName;

    private String inspectionType;

    private Long materialId;

    private String materialCode;

    private String materialName;

    private Long productId;

    private String productCode;

    private String productName;

    private String processCode;

    private String processName;

    private String inspectionItem;

    private String inspectionMethod;

    private String standardValue;

    private String toleranceMin;

    private String toleranceMax;

    private String unit;

    private Integer isKeyItem;

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
