package com.erp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * BOM信息VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "BOM信息")
public class BomVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "BOM ID")
    private Long id;

    @Schema(description = "父件ID")
    private Long parentId;

    @Schema(description = "父件类型：1产品 2物料")
    private Integer parentType;

    @Schema(description = "父件类型名称")
    private String parentTypeName;

    @Schema(description = "父件编码")
    private String parentCode;

    @Schema(description = "父件名称")
    private String parentName;

    @Schema(description = "子件ID")
    private Long childId;

    @Schema(description = "子件类型：1产品 2物料")
    private Integer childType;

    @Schema(description = "子件类型名称")
    private String childTypeName;

    @Schema(description = "子件编码")
    private String childCode;

    @Schema(description = "子件名称")
    private String childName;

    @Schema(description = "用量")
    private BigDecimal quantity;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态：1启用 0禁用")
    private Integer status;

    @Schema(description = "创建人")
    private String createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新人")
    private String updateBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
