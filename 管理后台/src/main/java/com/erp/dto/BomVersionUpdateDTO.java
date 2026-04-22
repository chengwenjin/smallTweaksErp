package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * BOM版本更新DTO
 */
@Data
@Schema(description = "BOM版本更新参数")
public class BomVersionUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "版本ID")
    private Long id;

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
}
