package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * BOM版本创建DTO
 */
@Data
@Schema(description = "BOM版本创建参数")
public class BomVersionCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

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
    private Integer status = 1;
}
