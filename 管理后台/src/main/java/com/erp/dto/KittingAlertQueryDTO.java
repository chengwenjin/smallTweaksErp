package com.erp.dto;

import com.baserbac.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Schema(description = "齐套预警查询参数")
public class KittingAlertQueryDTO extends PageQueryDTO {

    private static final long serialVersionUID = 1L;

    @Schema(description = "预警编号（模糊查询）")
    private String alertNo;

    @Schema(description = "MPS编号（模糊查询）")
    private String mpsNo;

    @Schema(description = "产品编码（模糊查询）")
    private String productCode;

    @Schema(description = "产品名称（模糊查询）")
    private String productName;

    @Schema(description = "物料编码（模糊查询）")
    private String materialCode;

    @Schema(description = "物料名称（模糊查询）")
    private String materialName;

    @Schema(description = "预警等级：1紧急 2高 3中 4低")
    private Integer alertLevel;

    @Schema(description = "需求日期")
    private LocalDate requiredDate;

    @Schema(description = "是否已同步SCM：0否 1是")
    private Integer syncedToScrm;

    @Schema(description = "状态：1待处理 2已跟催 3已解决 4已忽略")
    private Integer status;
}
