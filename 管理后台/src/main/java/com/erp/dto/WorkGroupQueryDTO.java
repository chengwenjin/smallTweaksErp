package com.erp.dto;

import com.baserbac.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "班组查询参数")
public class WorkGroupQueryDTO extends PageQueryDTO {

    private static final long serialVersionUID = 1L;

    @Schema(description = "班组编码（模糊查询）")
    private String groupCode;

    @Schema(description = "班组名称（模糊查询）")
    private String groupName;

    @Schema(description = "班组类型")
    private String groupType;

    @Schema(description = "车间")
    private String workshop;

    @Schema(description = "工作中心")
    private String workcenter;

    @Schema(description = "状态：1启用 0停用")
    private Integer status;
}
