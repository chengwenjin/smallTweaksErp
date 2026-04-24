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
@Schema(description = "班组信息")
public class WorkGroupVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "班组ID")
    private Long id;

    @Schema(description = "班组编码")
    private String groupCode;

    @Schema(description = "班组名称")
    private String groupName;

    @Schema(description = "班组类型")
    private String groupType;

    @Schema(description = "班组类型名称")
    private String groupTypeName;

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

    @Schema(description = "技能等级")
    private String skillLevel;

    @Schema(description = "技能等级名称")
    private String skillLevelName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态：1启用 0停用")
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
