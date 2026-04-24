package com.erp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "设备创建参数")
public class EquipmentCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "设备编码")
    private String equipmentCode;

    @Schema(description = "设备名称")
    private String equipmentName;

    @Schema(description = "设备类型")
    private String equipmentType;

    @Schema(description = "规格型号")
    private String specification;

    @Schema(description = "品牌")
    private String brand;

    @Schema(description = "型号")
    private String model;

    @Schema(description = "每小时产能")
    private BigDecimal capacityPerHour;

    @Schema(description = "产能单位")
    private String capacityUnit;

    @Schema(description = "购置日期")
    private LocalDate purchaseDate;

    @Schema(description = "保修到期日期")
    private LocalDate warrantyExpiryDate;

    @Schema(description = "车间")
    private String workshop;

    @Schema(description = "工作中心")
    private String workcenter;

    @Schema(description = "上次维护日期")
    private LocalDate maintenanceDate;

    @Schema(description = "维护间隔天数")
    private Integer maintenanceIntervalDays;

    @Schema(description = "负责人")
    private String responsiblePerson;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态：1运行中 2停机维护 3故障 0停用")
    private Integer status;
}
