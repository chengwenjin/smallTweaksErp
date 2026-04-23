package com.erp.service;

import com.baserbac.common.enums.ResultCode;
import com.baserbac.common.exception.BusinessException;
import com.baserbac.common.result.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.dto.DemandSourceQueryDTO;
import com.erp.entity.ErpDemandSource;
import com.erp.entity.ErpForecastOrder;
import com.erp.entity.ErpForecastOrderItem;
import com.erp.entity.ErpSalesOrder;
import com.erp.entity.ErpSalesOrderItem;
import com.erp.mapper.DemandSourceMapper;
import com.erp.mapper.ForecastOrderItemMapper;
import com.erp.mapper.ForecastOrderMapper;
import com.erp.mapper.SalesOrderItemMapper;
import com.erp.mapper.SalesOrderMapper;
import com.erp.vo.DemandSourceVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DemandSourceService {

    private final DemandSourceMapper demandSourceMapper;
    private final SalesOrderMapper salesOrderMapper;
    private final SalesOrderItemMapper salesOrderItemMapper;
    private final ForecastOrderMapper forecastOrderMapper;
    private final ForecastOrderItemMapper forecastOrderItemMapper;

    public PageResult<DemandSourceVO> pageDemandSources(DemandSourceQueryDTO queryDTO) {
        Page<ErpDemandSource> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<ErpDemandSource> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(queryDTO.getSourceNo() != null, ErpDemandSource::getSourceNo, queryDTO.getSourceNo())
               .eq(queryDTO.getSourceType() != null, ErpDemandSource::getSourceType, queryDTO.getSourceType())
               .like(queryDTO.getProductCode() != null, ErpDemandSource::getProductCode, queryDTO.getProductCode())
               .like(queryDTO.getProductName() != null, ErpDemandSource::getProductName, queryDTO.getProductName())
               .eq(queryDTO.getStatus() != null, ErpDemandSource::getStatus, queryDTO.getStatus())
               .orderByDesc(ErpDemandSource::getCreateTime);
        
        Page<ErpDemandSource> result = demandSourceMapper.selectPage(page, wrapper);
        
        List<DemandSourceVO> voList = result.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        return PageResult.of(
            result.getTotal(), 
            voList, 
            (long) result.getCurrent(), 
            (long) result.getSize()
        );
    }

    public DemandSourceVO getDemandSourceById(Long id) {
        ErpDemandSource demandSource = demandSourceMapper.selectById(id);
        if (demandSource == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return convertToVO(demandSource);
    }

    @Transactional(rollbackFor = Exception.class)
    public void syncFromSalesOrders() {
        log.info("开始从销售订单同步需求来源...");
        
        LambdaQueryWrapper<ErpSalesOrder> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.in(ErpSalesOrder::getStatus, 1, 2, 3)
                   .orderByAsc(ErpSalesOrder::getDeliveryDate);
        
        List<ErpSalesOrder> orders = salesOrderMapper.selectList(orderWrapper);
        
        for (ErpSalesOrder order : orders) {
            LambdaQueryWrapper<ErpSalesOrderItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(ErpSalesOrderItem::getOrderId, order.getId());
            
            List<ErpSalesOrderItem> items = salesOrderItemMapper.selectList(itemWrapper);
            
            for (ErpSalesOrderItem item : items) {
                LambdaQueryWrapper<ErpDemandSource> checkWrapper = new LambdaQueryWrapper<>();
                checkWrapper.eq(ErpDemandSource::getSourceType, 1)
                           .eq(ErpDemandSource::getSourceId, order.getId())
                           .eq(ErpDemandSource::getProductId, item.getProductId());
                
                Long count = demandSourceMapper.selectCount(checkWrapper);
                
                if (count == 0) {
                    ErpDemandSource demandSource = new ErpDemandSource();
                    String sourceNo = "DS" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + 
                        String.format("%06d", System.currentTimeMillis() % 1000000);
                    demandSource.setSourceNo(sourceNo);
                    demandSource.setSourceType(1);
                    demandSource.setSourceId(order.getId());
                    demandSource.setProductId(item.getProductId());
                    demandSource.setProductCode(item.getProductCode());
                    demandSource.setProductName(item.getProductName());
                    demandSource.setSpecification(item.getSpecification());
                    demandSource.setUnit(item.getUnit());
                    demandSource.setDemandQuantity(item.getQuantity());
                    demandSource.setDemandDate(order.getDeliveryDate());
                    demandSource.setAllocatedQuantity(java.math.BigDecimal.ZERO);
                    demandSource.setRemark("来自销售订单: " + order.getOrderNo());
                    demandSource.setStatus(1);
                    
                    demandSourceMapper.insert(demandSource);
                    log.info("同步销售订单需求来源: {} - {} - {}", order.getOrderNo(), item.getProductCode(), item.getQuantity());
                }
            }
        }
        
        log.info("销售订单需求来源同步完成");
    }

    @Transactional(rollbackFor = Exception.class)
    public void syncFromForecastOrders() {
        log.info("开始从预测单同步需求来源...");
        
        LambdaQueryWrapper<ErpForecastOrder> forecastWrapper = new LambdaQueryWrapper<>();
        forecastWrapper.in(ErpForecastOrder::getStatus, 1, 2)
                      .orderByAsc(ErpForecastOrder::getStartDate);
        
        List<ErpForecastOrder> forecasts = forecastOrderMapper.selectList(forecastWrapper);
        
        for (ErpForecastOrder forecast : forecasts) {
            LambdaQueryWrapper<ErpForecastOrderItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(ErpForecastOrderItem::getForecastId, forecast.getId());
            
            List<ErpForecastOrderItem> items = forecastOrderItemMapper.selectList(itemWrapper);
            
            for (ErpForecastOrderItem item : items) {
                LambdaQueryWrapper<ErpDemandSource> checkWrapper = new LambdaQueryWrapper<>();
                checkWrapper.eq(ErpDemandSource::getSourceType, 2)
                           .eq(ErpDemandSource::getSourceId, forecast.getId())
                           .eq(ErpDemandSource::getProductId, item.getProductId());
                
                Long count = demandSourceMapper.selectCount(checkWrapper);
                
                if (count == 0) {
                    ErpDemandSource demandSource = new ErpDemandSource();
                    String sourceNo = "DS" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + 
                        String.format("%06d", System.currentTimeMillis() % 1000000);
                    demandSource.setSourceNo(sourceNo);
                    demandSource.setSourceType(2);
                    demandSource.setSourceId(forecast.getId());
                    demandSource.setProductId(item.getProductId());
                    demandSource.setProductCode(item.getProductCode());
                    demandSource.setProductName(item.getProductName());
                    demandSource.setSpecification(item.getSpecification());
                    demandSource.setUnit(item.getUnit());
                    demandSource.setDemandQuantity(item.getForecastQuantity());
                    demandSource.setDemandDate(forecast.getStartDate());
                    demandSource.setAllocatedQuantity(java.math.BigDecimal.ZERO);
                    demandSource.setRemark("来自预测单: " + forecast.getForecastNo());
                    demandSource.setStatus(1);
                    
                    demandSourceMapper.insert(demandSource);
                    log.info("同步预测单需求来源: {} - {} - {}", forecast.getForecastNo(), item.getProductCode(), item.getForecastQuantity());
                }
            }
        }
        
        log.info("预测单需求来源同步完成");
    }

    @Transactional(rollbackFor = Exception.class)
    public void syncAllDemandSources() {
        syncFromSalesOrders();
        syncFromForecastOrders();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateDemandSourceStatus(Long id, Integer status) {
        ErpDemandSource demandSource = demandSourceMapper.selectById(id);
        if (demandSource == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        demandSource.setStatus(status);
        demandSourceMapper.updateById(demandSource);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteDemandSource(Long id) {
        ErpDemandSource demandSource = demandSourceMapper.selectById(id);
        if (demandSource == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        demandSourceMapper.deleteById(id);
    }

    private DemandSourceVO convertToVO(ErpDemandSource demandSource) {
        String sourceTypeName = switch (demandSource.getSourceType()) {
            case 1 -> "销售订单";
            case 2 -> "预测单";
            default -> "未知来源";
        };
        
        String statusName = switch (demandSource.getStatus()) {
            case 1 -> "待处理";
            case 2 -> "已处理";
            case 3 -> "已取消";
            default -> "未知状态";
        };
        
        return DemandSourceVO.builder()
            .id(demandSource.getId())
            .sourceNo(demandSource.getSourceNo())
            .sourceType(demandSource.getSourceType())
            .sourceTypeName(sourceTypeName)
            .sourceId(demandSource.getSourceId())
            .productId(demandSource.getProductId())
            .productCode(demandSource.getProductCode())
            .productName(demandSource.getProductName())
            .specification(demandSource.getSpecification())
            .unit(demandSource.getUnit())
            .demandQuantity(demandSource.getDemandQuantity())
            .demandDate(demandSource.getDemandDate())
            .allocatedQuantity(demandSource.getAllocatedQuantity())
            .remark(demandSource.getRemark())
            .status(demandSource.getStatus())
            .statusName(statusName)
            .createBy(demandSource.getCreateBy())
            .createTime(demandSource.getCreateTime())
            .updateBy(demandSource.getUpdateBy())
            .updateTime(demandSource.getUpdateTime())
            .build();
    }
}
