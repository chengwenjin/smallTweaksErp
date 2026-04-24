package com.erp.dto;

import com.baserbac.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "设备查询参数")
public class EquipmentQueryDTO extends PageQueryDTO {

    private static final long serialVersionUID = 1L;

    @Schema(description = "设备编码（模糊查询）")
    private String equipmentCode;

    @Schema(description = "设备名称（模糊查询）")
    private String equipmentName;

    @Schema(description = "设备类型")
    private String equipmentType;

    @Schema(description = "品牌")
    private String brand;

    @Schema(description = "车间")
    private String workshop;

    @Schema(description = "工作中心")
    private String workcenter;

    @Schema(description = "状态：1运行中 2停机维护 3故障 0停用")
    private Integer status;
}
