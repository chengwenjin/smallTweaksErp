package com.erp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "质量检验标准信息")
public class QualityStandardVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "标准编号")
    private String standardNo;

    @Schema(description = "标准名称")
    private String standardName;

    @Schema(description = "检验类型")
    private String inspectionType;

    @Schema(description = "检验类型名称")
    private String inspectionTypeName;

    @Schema(description = "物料ID")
    private Long materialId;

    @Schema(description = "物料编码")
    private String materialCode;

    @Schema(description = "物料名称")
    private String materialName;

    @Schema(description = "产品ID")
    private Long productId;

    @Schema(description = "产品编码")
    private String productCode;

    @Schema(description = "产品名称")
    private String productName;

    @Schema(description = "工序编码")
    private String processCode;

    @Schema(description = "工序名称")
    private String processName;

    @Schema(description = "检验项目")
    private String inspectionItem;

    @Schema(description = "检验方法")
    private String inspectionMethod;

    @Schema(description = "标准值")
    private String standardValue;

    @Schema(description = "公差下限")
    private String toleranceMin;

    @Schema(description = "公差上限")
    private String toleranceMax;

    @Schema(description = "计量单位")
    private String unit;

    @Schema(description = "是否关键项目")
    private Integer isKeyItem;

    @Schema(description = "是否关键项目名称")
    private String isKeyItemName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "创建人")
    private String createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新人")
    private String updateBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
