package com.erp.service;

import com.baserbac.common.enums.ResultCode;
import com.baserbac.common.exception.BusinessException;
import com.baserbac.common.result.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.dto.FqcInspectionCreateDTO;
import com.erp.dto.FqcInspectionDisposalDTO;
import com.erp.dto.FqcInspectionQueryDTO;
import com.erp.dto.FqcInspectionUpdateDTO;
import com.erp.entity.ErpFqcInspection;
import com.erp.mapper.FqcInspectionMapper;
import com.erp.vo.FqcInspectionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FqcInspectionService {

    private final FqcInspectionMapper fqcInspectionMapper;

    public static final int STATUS_DRAFT = 1;
    public static final int STATUS_PENDING_INSPECTION = 2;
    public static final int STATUS_INSPECTION_COMPLETED = 3;
    public static final int STATUS_DISPOSAL_PENDING = 4;
    public static final int STATUS_COMPLETED = 5;
    public static final int STATUS_CANCELLED = 6;

    private static final Map<Integer, String> STATUS_NAMES = new HashMap<>();
    static {
        STATUS_NAMES.put(STATUS_DRAFT, "草稿");
        STATUS_NAMES.put(STATUS_PENDING_INSPECTION, "待检验");
        STATUS_NAMES.put(STATUS_INSPECTION_COMPLETED, "检验完成");
        STATUS_NAMES.put(STATUS_DISPOSAL_PENDING, "待处理");
        STATUS_NAMES.put(STATUS_COMPLETED, "已完成");
        STATUS_NAMES.put(STATUS_CANCELLED, "已取消");
    }

    public static final String INSPECTION_TYPE_FULL = "FULL";
    public static final String INSPECTION_TYPE_SAMPLE = "SAMPLE";

    private static final Map<String, String> INSPECTION_TYPE_NAMES = new HashMap<>();
    static {
        INSPECTION_TYPE_NAMES.put(INSPECTION_TYPE_FULL, "全检");
        INSPECTION_TYPE_NAMES.put(INSPECTION_TYPE_SAMPLE, "抽检");
    }

    public static final String RESULT_QUALIFIED = "QUALIFIED";
    public static final String RESULT_UNQUALIFIED = "UNQUALIFIED";

    private static final Map<String, String> RESULT_NAMES = new HashMap<>();
    static {
        RESULT_NAMES.put(RESULT_QUALIFIED, "合格");
        RESULT_NAMES.put(RESULT_UNQUALIFIED, "不合格");
    }

    public static final String DISPOSAL_REWORK = "REWORK";
    public static final String DISPOSAL_SCRAP = "SCRAP";
    public static final String DISPOSAL_SPECIAL_ACCEPT = "SPECIAL_ACCEPT";

    private static final Map<String, String> DISPOSAL_TYPE_NAMES = new HashMap<>();
    static {
        DISPOSAL_TYPE_NAMES.put(DISPOSAL_REWORK, "返工");
        DISPOSAL_TYPE_NAMES.put(DISPOSAL_SCRAP, "报废");
        DISPOSAL_TYPE_NAMES.put(DISPOSAL_SPECIAL_ACCEPT, "特采");
    }

    public PageResult<FqcInspectionVO> pageFqcInspections(FqcInspectionQueryDTO queryDTO) {
        Page<ErpFqcInspection> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<ErpFqcInspection> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getInspectionNo()), 
                ErpFqcInspection::getInspectionNo, queryDTO.getInspectionNo())
               .like(StringUtils.hasText(queryDTO.getWorkOrderNo()), 
                ErpFqcInspection::getWorkOrderNo, queryDTO.getWorkOrderNo())
               .like(StringUtils.hasText(queryDTO.getProductCode()), 
                ErpFqcInspection::getProductCode, queryDTO.getProductCode())
               .like(StringUtils.hasText(queryDTO.getProductName()), 
                ErpFqcInspection::getProductName, queryDTO.getProductName())
               .like(StringUtils.hasText(queryDTO.getBatchNo()), 
                ErpFqcInspection::getBatchNo, queryDTO.getBatchNo())
               .eq(StringUtils.hasText(queryDTO.getInspectionResult()), 
                ErpFqcInspection::getInspectionResult, queryDTO.getInspectionResult())
               .like(StringUtils.hasText(queryDTO.getCertificateNo()), 
                ErpFqcInspection::getCertificateNo, queryDTO.getCertificateNo())
               .eq(queryDTO.getAllowWarehousing() != null, 
                ErpFqcInspection::getAllowWarehousing, queryDTO.getAllowWarehousing())
               .eq(queryDTO.getStatus() != null, ErpFqcInspection::getStatus, queryDTO.getStatus())
               .orderByDesc(ErpFqcInspection::getCreateTime);
        
        Page<ErpFqcInspection> result = fqcInspectionMapper.selectPage(page, wrapper);
        
        List<FqcInspectionVO> voList = result.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        return PageResult.of(
            result.getTotal(), 
            voList, 
            (long) result.getCurrent(), 
            (long) result.getSize()
        );
    }

    public FqcInspectionVO getFqcInspectionById(Long id) {
        ErpFqcInspection inspection = fqcInspectionMapper.selectById(id);
        if (inspection == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return convertToVO(inspection);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createFqcInspection(FqcInspectionCreateDTO createDTO) {
        ErpFqcInspection inspection = new ErpFqcInspection();
        
        String inspectionNo = generateInspectionNo();
        inspection.setInspectionNo(inspectionNo);
        
        inspection.setWorkOrderId(createDTO.getWorkOrderId());
        inspection.setWorkOrderNo(createDTO.getWorkOrderNo());
        inspection.setProductId(createDTO.getProductId());
        inspection.setProductCode(createDTO.getProductCode());
        inspection.setProductName(createDTO.getProductName());
        inspection.setSpecification(createDTO.getSpecification());
        inspection.setUnit(createDTO.getUnit());
        inspection.setBatchNo(createDTO.getBatchNo());
        inspection.setFinishedQuantity(createDTO.getFinishedQuantity() != null ? createDTO.getFinishedQuantity() : BigDecimal.ZERO);
        inspection.setInspectionQuantity(createDTO.getInspectionQuantity() != null ? createDTO.getInspectionQuantity() : BigDecimal.ZERO);
        inspection.setInspectionType(createDTO.getInspectionType() != null ? createDTO.getInspectionType() : INSPECTION_TYPE_FULL);
        inspection.setInspectionStandard(createDTO.getInspectionStandard());
        inspection.setInspectorId(createDTO.getInspectorId());
        inspection.setInspectorName(createDTO.getInspectorName());
        inspection.setRemark(createDTO.getRemark());
        inspection.setStatus(STATUS_DRAFT);
        inspection.setAllowWarehousing(0);
        
        fqcInspectionMapper.insert(inspection);
        
        log.info("创建成品检验单成功: {}", inspectionNo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateFqcInspection(FqcInspectionUpdateDTO updateDTO) {
        ErpFqcInspection inspection = fqcInspectionMapper.selectById(updateDTO.getId());
        if (inspection == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (updateDTO.getWorkOrderId() != null) {
            inspection.setWorkOrderId(updateDTO.getWorkOrderId());
        }
        if (updateDTO.getWorkOrderNo() != null) {
            inspection.setWorkOrderNo(updateDTO.getWorkOrderNo());
        }
        if (updateDTO.getProductId() != null) {
            inspection.setProductId(updateDTO.getProductId());
        }
        if (updateDTO.getProductCode() != null) {
            inspection.setProductCode(updateDTO.getProductCode());
        }
        if (updateDTO.getProductName() != null) {
            inspection.setProductName(updateDTO.getProductName());
        }
        if (updateDTO.getSpecification() != null) {
            inspection.setSpecification(updateDTO.getSpecification());
        }
        if (updateDTO.getUnit() != null) {
            inspection.setUnit(updateDTO.getUnit());
        }
        if (updateDTO.getBatchNo() != null) {
            inspection.setBatchNo(updateDTO.getBatchNo());
        }
        if (updateDTO.getFinishedQuantity() != null) {
            inspection.setFinishedQuantity(updateDTO.getFinishedQuantity());
        }
        if (updateDTO.getInspectionQuantity() != null) {
            inspection.setInspectionQuantity(updateDTO.getInspectionQuantity());
        }
        if (updateDTO.getInspectionType() != null) {
            inspection.setInspectionType(updateDTO.getInspectionType());
        }
        if (updateDTO.getInspectionStandard() != null) {
            inspection.setInspectionStandard(updateDTO.getInspectionStandard());
        }
        if (updateDTO.getInspectorId() != null) {
            inspection.setInspectorId(updateDTO.getInspectorId());
        }
        if (updateDTO.getInspectorName() != null) {
            inspection.setInspectorName(updateDTO.getInspectorName());
        }
        if (updateDTO.getRemark() != null) {
            inspection.setRemark(updateDTO.getRemark());
        }
        
        fqcInspectionMapper.updateById(inspection);
        
        log.info("更新成品检验单成功: {}", inspection.getInspectionNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteFqcInspection(Long id) {
        ErpFqcInspection inspection = fqcInspectionMapper.selectById(id);
        if (inspection == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (inspection.getStatus() != STATUS_DRAFT) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "只能删除草稿状态的检验单");
        }
        
        fqcInspectionMapper.deleteById(id);
        
        log.info("删除成品检验单成功: {}", inspection.getInspectionNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void submitInspection(Long id) {
        ErpFqcInspection inspection = fqcInspectionMapper.selectById(id);
        if (inspection == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (inspection.getStatus() != STATUS_DRAFT) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "只有草稿状态的检验单才能提交");
        }
        
        inspection.setStatus(STATUS_PENDING_INSPECTION);
        fqcInspectionMapper.updateById(inspection);
        
        log.info("成品检验单提交成功: {}", inspection.getInspectionNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void completeInspection(FqcInspectionUpdateDTO updateDTO) {
        ErpFqcInspection inspection = fqcInspectionMapper.selectById(updateDTO.getId());
        if (inspection == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (inspection.getStatus() != STATUS_PENDING_INSPECTION) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "只有待检验状态的检验单才能完成检验");
        }
        
        if (updateDTO.getQualifiedCount() != null) {
            inspection.setQualifiedCount(updateDTO.getQualifiedCount());
        }
        if (updateDTO.getUnqualifiedCount() != null) {
            inspection.setUnqualifiedCount(updateDTO.getUnqualifiedCount());
        }
        if (updateDTO.getReworkCount() != null) {
            inspection.setReworkCount(updateDTO.getReworkCount());
        }
        if (updateDTO.getScrappedCount() != null) {
            inspection.setScrappedCount(updateDTO.getScrappedCount());
        }
        
        int total = (inspection.getQualifiedCount() != null ? inspection.getQualifiedCount() : 0) +
                    (inspection.getUnqualifiedCount() != null ? inspection.getUnqualifiedCount() : 0);
        if (total > 0) {
            BigDecimal qualifiedRate = new BigDecimal(inspection.getQualifiedCount() != null ? inspection.getQualifiedCount() : 0)
                .divide(new BigDecimal(total), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
            inspection.setQualifiedRate(qualifiedRate);
        }
        
        if (updateDTO.getInspectionResult() != null) {
            inspection.setInspectionResult(updateDTO.getInspectionResult());
        } else {
            if (inspection.getUnqualifiedCount() != null && inspection.getUnqualifiedCount() > 0) {
                inspection.setInspectionResult(RESULT_UNQUALIFIED);
            } else {
                inspection.setInspectionResult(RESULT_QUALIFIED);
            }
        }
        
        if (RESULT_QUALIFIED.equals(inspection.getInspectionResult())) {
            inspection.setCertificateNo(generateCertificateNo());
            inspection.setCertificateDate(LocalDateTime.now());
            inspection.setAllowWarehousing(1);
        }
        
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(STATUS_INSPECTION_COMPLETED);
        
        if (RESULT_UNQUALIFIED.equals(inspection.getInspectionResult())) {
            inspection.setStatus(STATUS_DISPOSAL_PENDING);
            inspection.setAllowWarehousing(0);
        }
        
        fqcInspectionMapper.updateById(inspection);
        
        log.info("成品检验完成: {}, 结果: {}", inspection.getInspectionNo(), inspection.getInspectionResult());
    }

    @Transactional(rollbackFor = Exception.class)
    public void disposal(FqcInspectionDisposalDTO disposalDTO) {
        ErpFqcInspection inspection = fqcInspectionMapper.selectById(disposalDTO.getId());
        if (inspection == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (inspection.getStatus() != STATUS_DISPOSAL_PENDING) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "只有待处理状态的检验单才能进行处理");
        }
        
        if (disposalDTO.getDisposalType() != null) {
            inspection.setDisposalType(disposalDTO.getDisposalType());
        }
        if (disposalDTO.getDisposalRemark() != null) {
            inspection.setDisposalRemark(disposalDTO.getDisposalRemark());
        }
        if (disposalDTO.getInspectionResult() != null) {
            inspection.setInspectionResult(disposalDTO.getInspectionResult());
        }
        
        if (DISPOSAL_SPECIAL_ACCEPT.equals(inspection.getDisposalType())) {
            inspection.setAllowWarehousing(1);
            inspection.setCertificateNo(generateCertificateNo());
            inspection.setCertificateDate(LocalDateTime.now());
        }
        
        inspection.setDisposalDate(LocalDateTime.now());
        inspection.setStatus(STATUS_COMPLETED);
        
        fqcInspectionMapper.updateById(inspection);
        
        log.info("成品检验处理完成: {}, 处理方式: {}", inspection.getInspectionNo(), inspection.getDisposalType());
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long id) {
        ErpFqcInspection inspection = fqcInspectionMapper.selectById(id);
        if (inspection == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (inspection.getStatus() == STATUS_COMPLETED) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "已完成的检验单不能取消");
        }
        
        inspection.setStatus(STATUS_CANCELLED);
        fqcInspectionMapper.updateById(inspection);
        
        log.info("成品检验单取消成功: {}", inspection.getInspectionNo());
    }

    private String generateInspectionNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        Long count = fqcInspectionMapper.selectCount(
            new LambdaQueryWrapper<ErpFqcInspection>()
                .likeRight(ErpFqcInspection::getInspectionNo, "FQC" + dateStr)
        );
        
        long seq = (count != null ? count : 0) + 1;
        return String.format("FQC%s%04d", dateStr, seq);
    }

    private String generateCertificateNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        Long count = fqcInspectionMapper.selectCount(
            new LambdaQueryWrapper<ErpFqcInspection>()
                .likeRight(ErpFqcInspection::getCertificateNo, "QC" + dateStr)
        );
        
        long seq = (count != null ? count : 0) + 1;
        return String.format("QC%s%04d", dateStr, seq);
    }

    private FqcInspectionVO convertToVO(ErpFqcInspection inspection) {
        String statusName = STATUS_NAMES.get(inspection.getStatus());
        if (statusName == null) {
            statusName = "未知";
        }
        
        String inspectionTypeName = INSPECTION_TYPE_NAMES.get(inspection.getInspectionType());
        if (inspectionTypeName == null) {
            inspectionTypeName = "未知";
        }
        
        String inspectionResultName = RESULT_NAMES.get(inspection.getInspectionResult());
        if (inspectionResultName == null) {
            inspectionResultName = "未检验";
        }
        
        String disposalTypeName = DISPOSAL_TYPE_NAMES.get(inspection.getDisposalType());
        if (disposalTypeName == null && inspection.getDisposalType() != null) {
            disposalTypeName = inspection.getDisposalType();
        }
        
        String allowWarehousingName = inspection.getAllowWarehousing() != null && inspection.getAllowWarehousing() == 1 ? "允许入库" : "禁止入库";
        
        return FqcInspectionVO.builder()
            .id(inspection.getId())
            .inspectionNo(inspection.getInspectionNo())
            .workOrderId(inspection.getWorkOrderId())
            .workOrderNo(inspection.getWorkOrderNo())
            .productId(inspection.getProductId())
            .productCode(inspection.getProductCode())
            .productName(inspection.getProductName())
            .specification(inspection.getSpecification())
            .unit(inspection.getUnit())
            .batchNo(inspection.getBatchNo())
            .finishedQuantity(inspection.getFinishedQuantity())
            .inspectionQuantity(inspection.getInspectionQuantity())
            .inspectionType(inspection.getInspectionType())
            .inspectionTypeName(inspectionTypeName)
            .inspectionStandard(inspection.getInspectionStandard())
            .inspectionDate(inspection.getInspectionDate())
            .inspectorId(inspection.getInspectorId())
            .inspectorName(inspection.getInspectorName())
            .qualifiedCount(inspection.getQualifiedCount())
            .unqualifiedCount(inspection.getUnqualifiedCount())
            .reworkCount(inspection.getReworkCount())
            .scrappedCount(inspection.getScrappedCount())
            .qualifiedRate(inspection.getQualifiedRate())
            .inspectionResult(inspection.getInspectionResult())
            .inspectionResultName(inspectionResultName)
            .certificateNo(inspection.getCertificateNo())
            .certificateDate(inspection.getCertificateDate())
            .disposalType(inspection.getDisposalType())
            .disposalTypeName(disposalTypeName)
            .disposalRemark(inspection.getDisposalRemark())
            .disposalDate(inspection.getDisposalDate())
            .disposalUserId(inspection.getDisposalUserId())
            .disposalUserName(inspection.getDisposalUserName())
            .allowWarehousing(inspection.getAllowWarehousing())
            .allowWarehousingName(allowWarehousingName)
            .warehouseOrderId(inspection.getWarehouseOrderId())
            .warehouseOrderNo(inspection.getWarehouseOrderNo())
            .remark(inspection.getRemark())
            .status(inspection.getStatus())
            .statusName(statusName)
            .createBy(inspection.getCreateBy())
            .createTime(inspection.getCreateTime())
            .updateBy(inspection.getUpdateBy())
            .updateTime(inspection.getUpdateTime())
            .build();
    }
}
