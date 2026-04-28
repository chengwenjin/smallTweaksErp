package com.erp.service;

import com.baserbac.common.enums.ResultCode;
import com.baserbac.common.exception.BusinessException;
import com.baserbac.common.result.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.dto.IqcInspectionCreateDTO;
import com.erp.dto.IqcInspectionDisposalDTO;
import com.erp.dto.IqcInspectionQueryDTO;
import com.erp.dto.IqcInspectionUpdateDTO;
import com.erp.entity.ErpIqcInspection;
import com.erp.mapper.IqcInspectionMapper;
import com.erp.vo.IqcInspectionVO;
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
public class IqcInspectionService {

    private final IqcInspectionMapper iqcInspectionMapper;

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

    public static final String DISPOSAL_RETURN = "RETURN";
    public static final String DISPOSAL_SPECIAL_ACCEPT = "SPECIAL_ACCEPT";
    public static final String DISPOSAL_REWORK = "REWORK";
    public static final String DISPOSAL_SCRAP = "SCRAP";

    private static final Map<String, String> DISPOSAL_TYPE_NAMES = new HashMap<>();
    static {
        DISPOSAL_TYPE_NAMES.put(DISPOSAL_RETURN, "退货");
        DISPOSAL_TYPE_NAMES.put(DISPOSAL_SPECIAL_ACCEPT, "特采");
        DISPOSAL_TYPE_NAMES.put(DISPOSAL_REWORK, "返工");
        DISPOSAL_TYPE_NAMES.put(DISPOSAL_SCRAP, "报废");
    }

    public PageResult<IqcInspectionVO> pageIqcInspections(IqcInspectionQueryDTO queryDTO) {
        Page<ErpIqcInspection> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<ErpIqcInspection> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getInspectionNo()), 
                ErpIqcInspection::getInspectionNo, queryDTO.getInspectionNo())
               .like(StringUtils.hasText(queryDTO.getPurchaseOrderNo()), 
                ErpIqcInspection::getPurchaseOrderNo, queryDTO.getPurchaseOrderNo())
               .like(StringUtils.hasText(queryDTO.getSupplierName()), 
                ErpIqcInspection::getSupplierName, queryDTO.getSupplierName())
               .like(StringUtils.hasText(queryDTO.getMaterialCode()), 
                ErpIqcInspection::getMaterialCode, queryDTO.getMaterialCode())
               .like(StringUtils.hasText(queryDTO.getMaterialName()), 
                ErpIqcInspection::getMaterialName, queryDTO.getMaterialName())
               .like(StringUtils.hasText(queryDTO.getBatchNo()), 
                ErpIqcInspection::getBatchNo, queryDTO.getBatchNo())
               .eq(StringUtils.hasText(queryDTO.getInspectionResult()), 
                ErpIqcInspection::getInspectionResult, queryDTO.getInspectionResult())
               .eq(queryDTO.getStatus() != null, ErpIqcInspection::getStatus, queryDTO.getStatus())
               .orderByDesc(ErpIqcInspection::getCreateTime);
        
        Page<ErpIqcInspection> result = iqcInspectionMapper.selectPage(page, wrapper);
        
        List<IqcInspectionVO> voList = result.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        return PageResult.of(
            result.getTotal(), 
            voList, 
            (long) result.getCurrent(), 
            (long) result.getSize()
        );
    }

    public IqcInspectionVO getIqcInspectionById(Long id) {
        ErpIqcInspection inspection = iqcInspectionMapper.selectById(id);
        if (inspection == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return convertToVO(inspection);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createIqcInspection(IqcInspectionCreateDTO createDTO) {
        ErpIqcInspection inspection = new ErpIqcInspection();
        
        String inspectionNo = generateInspectionNo();
        inspection.setInspectionNo(inspectionNo);
        
        inspection.setPurchaseOrderId(createDTO.getPurchaseOrderId());
        inspection.setPurchaseOrderNo(createDTO.getPurchaseOrderNo());
        inspection.setSupplierId(createDTO.getSupplierId());
        inspection.setSupplierCode(createDTO.getSupplierCode());
        inspection.setSupplierName(createDTO.getSupplierName());
        inspection.setMaterialId(createDTO.getMaterialId());
        inspection.setMaterialCode(createDTO.getMaterialCode());
        inspection.setMaterialName(createDTO.getMaterialName());
        inspection.setSpecification(createDTO.getSpecification());
        inspection.setUnit(createDTO.getUnit());
        inspection.setBatchNo(createDTO.getBatchNo());
        inspection.setReceivedQuantity(createDTO.getReceivedQuantity() != null ? createDTO.getReceivedQuantity() : BigDecimal.ZERO);
        inspection.setSampleQuantity(createDTO.getSampleQuantity() != null ? createDTO.getSampleQuantity() : BigDecimal.ZERO);
        inspection.setInspectionType(createDTO.getInspectionType() != null ? createDTO.getInspectionType() : INSPECTION_TYPE_SAMPLE);
        inspection.setInspectionStandard(createDTO.getInspectionStandard());
        inspection.setInspectorId(createDTO.getInspectorId());
        inspection.setInspectorName(createDTO.getInspectorName());
        inspection.setRemark(createDTO.getRemark());
        inspection.setStatus(STATUS_DRAFT);
        
        iqcInspectionMapper.insert(inspection);
        
        log.info("创建来料检验单成功: {}", inspectionNo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateIqcInspection(IqcInspectionUpdateDTO updateDTO) {
        ErpIqcInspection inspection = iqcInspectionMapper.selectById(updateDTO.getId());
        if (inspection == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (updateDTO.getSupplierId() != null) {
            inspection.setSupplierId(updateDTO.getSupplierId());
        }
        if (updateDTO.getSupplierCode() != null) {
            inspection.setSupplierCode(updateDTO.getSupplierCode());
        }
        if (updateDTO.getSupplierName() != null) {
            inspection.setSupplierName(updateDTO.getSupplierName());
        }
        if (updateDTO.getMaterialId() != null) {
            inspection.setMaterialId(updateDTO.getMaterialId());
        }
        if (updateDTO.getMaterialCode() != null) {
            inspection.setMaterialCode(updateDTO.getMaterialCode());
        }
        if (updateDTO.getMaterialName() != null) {
            inspection.setMaterialName(updateDTO.getMaterialName());
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
        if (updateDTO.getReceivedQuantity() != null) {
            inspection.setReceivedQuantity(updateDTO.getReceivedQuantity());
        }
        if (updateDTO.getSampleQuantity() != null) {
            inspection.setSampleQuantity(updateDTO.getSampleQuantity());
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
        
        iqcInspectionMapper.updateById(inspection);
        
        log.info("更新来料检验单成功: {}", inspection.getInspectionNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteIqcInspection(Long id) {
        ErpIqcInspection inspection = iqcInspectionMapper.selectById(id);
        if (inspection == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (inspection.getStatus() != STATUS_DRAFT) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "只能删除草稿状态的检验单");
        }
        
        iqcInspectionMapper.deleteById(id);
        
        log.info("删除来料检验单成功: {}", inspection.getInspectionNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void submitInspection(Long id) {
        ErpIqcInspection inspection = iqcInspectionMapper.selectById(id);
        if (inspection == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (inspection.getStatus() != STATUS_DRAFT) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "只有草稿状态的检验单才能提交");
        }
        
        inspection.setStatus(STATUS_PENDING_INSPECTION);
        iqcInspectionMapper.updateById(inspection);
        
        log.info("来料检验单提交成功: {}", inspection.getInspectionNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void completeInspection(IqcInspectionUpdateDTO updateDTO) {
        ErpIqcInspection inspection = iqcInspectionMapper.selectById(updateDTO.getId());
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
        
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(STATUS_INSPECTION_COMPLETED);
        
        if (RESULT_UNQUALIFIED.equals(inspection.getInspectionResult())) {
            inspection.setStatus(STATUS_DISPOSAL_PENDING);
        }
        
        iqcInspectionMapper.updateById(inspection);
        
        log.info("来料检验完成: {}, 结果: {}", inspection.getInspectionNo(), inspection.getInspectionResult());
    }

    @Transactional(rollbackFor = Exception.class)
    public void disposal(IqcInspectionDisposalDTO disposalDTO) {
        ErpIqcInspection inspection = iqcInspectionMapper.selectById(disposalDTO.getId());
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
        
        inspection.setDisposalDate(LocalDateTime.now());
        inspection.setStatus(STATUS_COMPLETED);
        
        iqcInspectionMapper.updateById(inspection);
        
        log.info("来料检验处理完成: {}, 处理方式: {}", inspection.getInspectionNo(), inspection.getDisposalType());
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long id) {
        ErpIqcInspection inspection = iqcInspectionMapper.selectById(id);
        if (inspection == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (inspection.getStatus() == STATUS_COMPLETED) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "已完成的检验单不能取消");
        }
        
        inspection.setStatus(STATUS_CANCELLED);
        iqcInspectionMapper.updateById(inspection);
        
        log.info("来料检验单取消成功: {}", inspection.getInspectionNo());
    }

    private String generateInspectionNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        Long count = iqcInspectionMapper.selectCount(
            new LambdaQueryWrapper<ErpIqcInspection>()
                .likeRight(ErpIqcInspection::getInspectionNo, "IQC" + dateStr)
        );
        
        long seq = (count != null ? count : 0) + 1;
        return String.format("IQC%s%04d", dateStr, seq);
    }

    private IqcInspectionVO convertToVO(ErpIqcInspection inspection) {
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
        
        return IqcInspectionVO.builder()
            .id(inspection.getId())
            .inspectionNo(inspection.getInspectionNo())
            .purchaseOrderId(inspection.getPurchaseOrderId())
            .purchaseOrderNo(inspection.getPurchaseOrderNo())
            .supplierId(inspection.getSupplierId())
            .supplierCode(inspection.getSupplierCode())
            .supplierName(inspection.getSupplierName())
            .materialId(inspection.getMaterialId())
            .materialCode(inspection.getMaterialCode())
            .materialName(inspection.getMaterialName())
            .specification(inspection.getSpecification())
            .unit(inspection.getUnit())
            .batchNo(inspection.getBatchNo())
            .receivedQuantity(inspection.getReceivedQuantity())
            .sampleQuantity(inspection.getSampleQuantity())
            .inspectionType(inspection.getInspectionType())
            .inspectionTypeName(inspectionTypeName)
            .inspectionStandard(inspection.getInspectionStandard())
            .inspectionDate(inspection.getInspectionDate())
            .inspectorId(inspection.getInspectorId())
            .inspectorName(inspection.getInspectorName())
            .qualifiedCount(inspection.getQualifiedCount())
            .unqualifiedCount(inspection.getUnqualifiedCount())
            .qualifiedRate(inspection.getQualifiedRate())
            .inspectionResult(inspection.getInspectionResult())
            .inspectionResultName(inspectionResultName)
            .disposalType(inspection.getDisposalType())
            .disposalTypeName(disposalTypeName)
            .disposalRemark(inspection.getDisposalRemark())
            .disposalDate(inspection.getDisposalDate())
            .disposalUserId(inspection.getDisposalUserId())
            .disposalUserName(inspection.getDisposalUserName())
            .workOrderId(inspection.getWorkOrderId())
            .workOrderNo(inspection.getWorkOrderNo())
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
