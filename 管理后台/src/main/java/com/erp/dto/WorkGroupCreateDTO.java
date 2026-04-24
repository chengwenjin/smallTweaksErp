package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "班组创建参数")
public class WorkGroupCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "班组编码")
    private String groupCode;

    @Schema(description = "班组名称")
    private String groupName;

    @Schema(description = "班组类型：1生产班 2维修班 3质检班")
    private String groupType;

    @Schema(description = "班组长")
    private String supervisor;

    @Schema(description = "班组长电话")
    private String supervisorPhone;

    @Schema(description = "车间")
    private String workshop;

    @Schema(description = "工作中心")
    private String workcenter;

    @Schema(description = "成员数量")
    private Integer memberCount;

    @Schema(description = "技能等级：1初级 2中级 3高级 4专家级")
    private String skillLevel;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态：1启用 0停用")
    private Integer status;
}
