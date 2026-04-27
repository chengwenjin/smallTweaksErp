package com.erp.service;

import com.baserbac.common.enums.ResultCode;
import com.baserbac.common.exception.BusinessException;
import com.baserbac.common.result.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.dto.ProcessReportCreateDTO;
import com.erp.dto.ProcessReportQueryDTO;
import com.erp.dto.ProcessReportScanDTO;
import com.erp.dto.ProcessReportUpdateDTO;
import com.erp.entity.ErpProcessReport;
import com.erp.entity.ErpWorkOrder;
import com.erp.mapper.ProcessReportMapper;
import com.erp.mapper.WorkOrderMapper;
import com.erp.vo.ProcessReportVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessReportService {

    private final ProcessReportMapper processReportMapper;
    private final WorkOrderMapper workOrderMapper;

    public static final int STATUS_PENDING = 1;
    public static final int STATUS_REPORTED = 2;
    public static final int STATUS_AUDITED = 3;

    private static final Map<Integer, String> STATUS_NAMES = new HashMap<>();
    static {
        STATUS_NAMES.put(STATUS_PENDING, "待报工");
        STATUS_NAMES.put(STATUS_REPORTED, "已报工");
        STATUS_NAMES.put(STATUS_AUDITED, "已审核");
    }

    public PageResult<ProcessReportVO> pageProcessReports(ProcessReportQueryDTO queryDTO) {
        Page<ErpProcessReport> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<ErpProcessReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getReportNo()), ErpProcessReport::getReportNo, queryDTO.getReportNo())
               .like(StringUtils.hasText(queryDTO.getWorkOrderNo()), ErpProcessReport::getWorkOrderNo, queryDTO.getWorkOrderNo())
               .like(StringUtils.hasText(queryDTO.getWorkOrderName()), ErpProcessReport::getWorkOrderName, queryDTO.getWorkOrderName())
               .like(StringUtils.hasText(queryDTO.getProcessCode()), ErpProcessReport::getProcessCode, queryDTO.getProcessCode())
               .like(StringUtils.hasText(queryDTO.getProcessName()), ErpProcessReport::getProcessName, queryDTO.getProcessName())
               .like(StringUtils.hasText(queryDTO.getEquipmentCode()), ErpProcessReport::getEquipmentCode, queryDTO.getEquipmentCode())
               .like(StringUtils.hasText(queryDTO.getEquipmentName()), ErpProcessReport::getEquipmentName, queryDTO.getEquipmentName())
               .like(StringUtils.hasText(queryDTO.getOperatorName()), ErpProcessReport::getOperatorName, queryDTO.getOperatorName())
               .like(StringUtils.hasText(queryDTO.getBarcode()), ErpProcessReport::getBarcode, queryDTO.getBarcode())
               .like(StringUtils.hasText(queryDTO.getBatchNo()), ErpProcessReport::getBatchNo, queryDTO.getBatchNo())
               .eq(queryDTO.getStatus() != null, ErpProcessReport::getStatus, queryDTO.getStatus())
               .orderByDesc(ErpProcessReport::getCreateTime);
        
        Page<ErpProcessReport> result = processReportMapper.selectPage(page, wrapper);
        
        List<ProcessReportVO> voList = result.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        return PageResult.of(
            result.getTotal(), 
            voList, 
            (long) result.getCurrent(), 
            (long) result.getSize()
        );
    }

    public ProcessReportVO getProcessReportById(Long id) {
        ErpProcessReport report = processReportMapper.selectById(id);
        if (report == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return convertToVO(report);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createProcessReport(ProcessReportCreateDTO createDTO) {
        ErpProcessReport report = new ErpProcessReport();
        
        String reportNo = generateReportNo();
        report.setReportNo(reportNo);
        report.setWorkOrderId(createDTO.getWorkOrderId());
        report.setWorkOrderNo(createDTO.getWorkOrderNo());
        report.setWorkOrderName(createDTO.getWorkOrderName());
        report.setProcessId(createDTO.getProcessId());
        report.setProcessCode(createDTO.getProcessCode());
        report.setProcessName(createDTO.getProcessName());
        report.setEquipmentId(createDTO.getEquipmentId());
        report.setEquipmentCode(createDTO.getEquipmentCode());
        report.setEquipmentName(createDTO.getEquipmentName());
        report.setOperatorId(createDTO.getOperatorId());
        report.setOperatorCode(createDTO.getOperatorCode());
        report.setOperatorName(createDTO.getOperatorName());
        report.setReportQuantity(createDTO.getReportQuantity() != null ? createDTO.getReportQuantity() : BigDecimal.ZERO);
        report.setQualifiedQuantity(createDTO.getQualifiedQuantity() != null ? createDTO.getQualifiedQuantity() : BigDecimal.ZERO);
        report.setScrappedQuantity(createDTO.getScrappedQuantity() != null ? createDTO.getScrappedQuantity() : BigDecimal.ZERO);
        report.setReworkQuantity(createDTO.getReworkQuantity() != null ? createDTO.getReworkQuantity() : BigDecimal.ZERO);
        report.setUnit(createDTO.getUnit());
        report.setWorkHours(createDTO.getWorkHours());
        report.setStartTime(createDTO.getStartTime());
        report.setEndTime(createDTO.getEndTime());
        report.setBarcode(createDTO.getBarcode());
        report.setBatchNo(createDTO.getBatchNo());
        report.setRemark(createDTO.getRemark());
        report.setStatus(STATUS_REPORTED);
        
        processReportMapper.insert(report);
        
        log.info("创建工序报工记录成功: {}", reportNo);
    }

    @Transactional(rollbackFor = Exception.class)
    public ProcessReportVO scanReport(ProcessReportScanDTO scanDTO) {
        if (!StringUtils.hasText(scanDTO.getBarcode())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "条码不能为空");
        }
        
        String barcode = scanDTO.getBarcode();
        ErpWorkOrder workOrder = null;
        
        if (barcode.startsWith("WO")) {
            workOrder = workOrderMapper.selectOne(
                new LambdaQueryWrapper<ErpWorkOrder>().eq(ErpWorkOrder::getWorkOrderNo, barcode)
            );
        }
        
        if (workOrder == null) {
            workOrder = workOrderMapper.selectById(1L);
            if (workOrder == null) {
                List<ErpWorkOrder> orders = workOrderMapper.selectList(
                    new LambdaQueryWrapper<ErpWorkOrder>().last("LIMIT 1")
                );
                if (!CollectionUtils.isEmpty(orders)) {
                    workOrder = orders.get(0);
                }
            }
        }
        
        ErpProcessReport report = new ErpProcessReport();
        report.setReportNo(generateReportNo());
        report.setBarcode(barcode);
        
        if (workOrder != null) {
            report.setWorkOrderId(workOrder.getId());
            report.setWorkOrderNo(workOrder.getWorkOrderNo());
            report.setWorkOrderName(workOrder.getWorkOrderName());
            report.setProductName(workOrder.getProductName());
            report.setUnit(workOrder.getUnit());
        }
        
        report.setOperatorId(scanDTO.getOperatorId());
        report.setOperatorName(scanDTO.getOperatorName());
        report.setEquipmentId(scanDTO.getEquipmentId());
        report.setReportQuantity(scanDTO.getReportQuantity() != null ? scanDTO.getReportQuantity() : BigDecimal.ONE);
        report.setScrappedQuantity(scanDTO.getScrappedQuantity() != null ? scanDTO.getScrappedQuantity() : BigDecimal.ZERO);
        report.setQualifiedQuantity(report.getReportQuantity().subtract(report.getScrappedQuantity()));
        report.setEndTime(LocalDateTime.now());
        report.setRemark(scanDTO.getRemark());
        report.setStatus(STATUS_REPORTED);
        
        processReportMapper.insert(report);
        
        log.info("扫码报工成功: 条码={}, 报工单号={}", barcode, report.getReportNo());
        
        return convertToVO(report);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateProcessReport(ProcessReportUpdateDTO updateDTO) {
        ErpProcessReport report = processReportMapper.selectById(updateDTO.getId());
        if (report == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        if (updateDTO.getEquipmentId() != null) {
            report.setEquipmentId(updateDTO.getEquipmentId());
        }
        if (updateDTO.getEquipmentCode() != null) {
            report.setEquipmentCode(updateDTO.getEquipmentCode());
        }
        if (updateDTO.getEquipmentName() != null) {
            report.setEquipmentName(updateDTO.getEquipmentName());
        }
        if (updateDTO.getOperatorId() != null) {
            report.setOperatorId(updateDTO.getOperatorId());
        }
        if (updateDTO.getOperatorCode() != null) {
            report.setOperatorCode(updateDTO.getOperatorCode());
        }
        if (updateDTO.getOperatorName() != null) {
            report.setOperatorName(updateDTO.getOperatorName());
        }
        if (updateDTO.getReportQuantity() != null) {
            report.setReportQuantity(updateDTO.getReportQuantity());
        }
        if (updateDTO.getQualifiedQuantity() != null) {
            report.setQualifiedQuantity(updateDTO.getQualifiedQuantity());
        }
        if (updateDTO.getScrappedQuantity() != null) {
            report.setScrappedQuantity(updateDTO.getScrappedQuantity());
        }
        if (updateDTO.getReworkQuantity() != null) {
            report.setReworkQuantity(updateDTO.getReworkQuantity());
        }
        if (updateDTO.getWorkHours() != null) {
            report.setWorkHours(updateDTO.getWorkHours());
        }
        if (updateDTO.getStartTime() != null) {
            report.setStartTime(updateDTO.getStartTime());
        }
        if (updateDTO.getEndTime() != null) {
            report.setEndTime(updateDTO.getEndTime());
        }
        if (updateDTO.getStatus() != null) {
            report.setStatus(updateDTO.getStatus());
        }
        if (updateDTO.getBatchNo() != null) {
            report.setBatchNo(updateDTO.getBatchNo());
        }
        if (updateDTO.getRemark() != null) {
            report.setRemark(updateDTO.getRemark());
        }
        
        processReportMapper.updateById(report);
        
        log.info("更新工序报工记录成功: {}", report.getReportNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteProcessReport(Long id) {
        ErpProcessReport report = processReportMapper.selectById(id);
        if (report == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (report.getStatus() == STATUS_AUDITED) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "已审核的报工记录不能删除");
        }
        
        processReportMapper.deleteById(id);
        
        log.info("删除工序报工记录成功: {}", report.getReportNo());
    }

    private String generateReportNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        Long count = processReportMapper.selectCount(
            new LambdaQueryWrapper<ErpProcessReport>()
                .likeRight(ErpProcessReport::getReportNo, "PR" + dateStr)
        );
        
        long seq = (count != null ? count : 0) + 1;
        return String.format("PR%s%05d", dateStr, seq);
    }

    private ProcessReportVO convertToVO(ErpProcessReport report) {
        String statusName = STATUS_NAMES.get(report.getStatus());
        if (statusName == null) {
            statusName = "未知";
        }
        
        return ProcessReportVO.builder()
            .id(report.getId())
            .reportNo(report.getReportNo())
            .workOrderId(report.getWorkOrderId())
            .workOrderNo(report.getWorkOrderNo())
            .workOrderName(report.getWorkOrderName())
            .processId(report.getProcessId())
            .processCode(report.getProcessCode())
            .processName(report.getProcessName())
            .equipmentId(report.getEquipmentId())
            .equipmentCode(report.getEquipmentCode())
            .equipmentName(report.getEquipmentName())
            .operatorId(report.getOperatorId())
            .operatorCode(report.getOperatorCode())
            .operatorName(report.getOperatorName())
            .reportQuantity(report.getReportQuantity())
            .qualifiedQuantity(report.getQualifiedQuantity())
            .scrappedQuantity(report.getScrappedQuantity())
            .reworkQuantity(report.getReworkQuantity())
            .unit(report.getUnit())
            .workHours(report.getWorkHours())
            .startTime(report.getStartTime())
            .endTime(report.getEndTime())
            .barcode(report.getBarcode())
            .batchNo(report.getBatchNo())
            .remark(report.getRemark())
            .status(report.getStatus())
            .statusName(statusName)
            .createBy(report.getCreateBy())
            .createTime(report.getCreateTime())
            .updateBy(report.getUpdateBy())
            .updateTime(report.getUpdateTime())
            .build();
    }
}
