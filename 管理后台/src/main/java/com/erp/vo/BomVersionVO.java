package com.erp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * BOM版本信息VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "BOM版本信息")
public class BomVersionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "版本ID")
    private Long id;

    @Schema(description = "BOM ID")
    private Long bomId;

    @Schema(description = "版本号")
    private String version;

    @Schema(description = "ECN编号")
    private String ecnNumber;

    @Schema(description = "变更原因")
    private String changeReason;

    @Schema(description = "变更内容")
    private String changeContent;

    @Schema(description = "生效时间")
    private LocalDateTime effectiveTime;

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
