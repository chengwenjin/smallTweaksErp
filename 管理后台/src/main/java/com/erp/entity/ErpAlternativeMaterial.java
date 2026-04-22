package com.erp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 替代料表实体
 */
@Data
@TableName("erp_alternative_material")
public class ErpAlternativeMaterial implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 主料ID（对应erp_product或erp_material的ID）
     */
    private Long mainMaterialId;

    /**
     * 主料类型：1产品 2物料
     */
    private Integer mainMaterialType;

    /**
     * 替代料ID（对应erp_product或erp_material的ID）
     */
    private Long alternativeMaterialId;

    /**
     * 替代料类型：1产品 2物料
     */
    private Integer alternativeMaterialType;

    /**
     * 替代比例
     */
    private BigDecimal alternativeRatio;

    /**
     * 优先级：1-5（1最高）
     */
    private Integer priority;

    /**
     * 适用场景
     */
    private String applicableScene;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态：1启用 0禁用
     */
    private Integer status;

    /**
     * 软删除：0未删除 1已删除
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 预留扩展字段1
     */
    private String ext1;

    /**
     * 预留扩展字段2
     */
    private String ext2;

    /**
     * 预留扩展字段3
     */
    private String ext3;
}
