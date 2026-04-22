package com.erp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 物料主数据表实体
 */
@Data
@TableName("erp_material")
public class ErpMaterial implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 物料编码（全局唯一）
     */
    private String materialCode;

    /**
     * 物料名称
     */
    private String materialName;

    /**
     * 物料类型：1原材料 2半成品 3成品
     */
    private Integer materialType;

    /**
     * 规格型号
     */
    private String specification;

    /**
     * 计量单位
     */
    private String unit;

    /**
     * 物料分类
     */
    private String category;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 自定义属性（JSON格式，如：{"颜色":"红色","尺寸":"M","材质":"纯棉"}）
     */
    private String customAttributes;

    /**
     * 描述
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
