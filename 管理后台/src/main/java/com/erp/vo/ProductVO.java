package com.erp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 产品信息VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "产品信息")
public class ProductVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "产品ID")
    private Long id;

    @Schema(description = "产品编码")
    private String productCode;

    @Schema(description = "产品名称")
    private String productName;

    @Schema(description = "规格型号")
    private String specification;

    @Schema(description = "计量单位")
    private String unit;

    @Schema(description = "产品分类")
    private String category;

    @Schema(description = "品牌")
    private String brand;

    @Schema(description = "技术参数（JSON格式）")
    private String technicalParams;

    @Schema(description = "产品描述")
    private String description;

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
