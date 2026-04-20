package com.erp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 产品档案表实体
 */
@Data
@TableName("erp_product")
public class ErpProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 产品编码（全局唯一）
     */
    private String productCode;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 规格型号
     */
    private String specification;

    /**
     * 计量单位
     */
    private String unit;

    /**
     * 产品分类
     */
    private String category;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 技术参数（JSON格式）
     */
    private String technicalParams;

    /**
     * 产品描述
     */
    private String description;

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
