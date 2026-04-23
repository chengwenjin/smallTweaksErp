package com.erp.service;

import com.baserbac.common.enums.ResultCode;
import com.baserbac.common.exception.BusinessException;
import com.baserbac.common.result.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.dto.ForecastOrderCreateDTO;
import com.erp.dto.ForecastOrderQueryDTO;
import com.erp.dto.ForecastOrderUpdateDTO;
import com.erp.entity.ErpForecastOrder;
import com.erp.mapper.ForecastOrderMapper;
import com.erp.vo.ForecastOrderVO;
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
public class ForecastOrderService {

    private final ForecastOrderMapper forecastOrderMapper;

    public PageResult<ForecastOrderVO> pageForecastOrders(ForecastOrderQueryDTO queryDTO) {
        Page<ErpForecastOrder> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<ErpForecastOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(queryDTO.getForecastNo() != null, ErpForecastOrder::getForecastNo, queryDTO.getForecastNo())
               .like(queryDTO.getForecastName() != null, ErpForecastOrder::getForecastName, queryDTO.getForecastName())
               .eq(queryDTO.getForecastType() != null, ErpForecastOrder::getForecastType, queryDTO.getForecastType())
               .eq(queryDTO.getStatus() != null, ErpForecastOrder::getStatus, queryDTO.getStatus())
               .ge(queryDTO.getStartDate() != null, ErpForecastOrder::getStartDate, queryDTO.getStartDate())
               .le(queryDTO.getEndDate() != null, ErpForecastOrder::getEndDate, queryDTO.getEndDate())
               .orderByDesc(ErpForecastOrder::getCreateTime);
        
        Page<ErpForecastOrder> result = forecastOrderMapper.selectPage(page, wrapper);
        
        List<ForecastOrderVO> voList = result.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        return PageResult.of(
            result.getTotal(), 
            voList, 
            (long) result.getCurrent(), 
            (long) result.getSize()
        );
    }

    public ForecastOrderVO getForecastOrderById(Long id) {
        ErpForecastOrder order = forecastOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return convertToVO(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createForecastOrder(ForecastOrderCreateDTO createDTO) {
        ErpForecastOrder order = new ErpForecastOrder();
        
        if (createDTO.getForecastNo() == null || createDTO.getForecastNo().isEmpty()) {
            String forecastNo = "FC" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + 
                String.format("%04d", System.currentTimeMillis() % 10000);
            order.setForecastNo(forecastNo);
        } else {
            Long count = forecastOrderMapper.selectCount(
                new LambdaQueryWrapper<ErpForecastOrder>()
                    .eq(ErpForecastOrder::getForecastNo, createDTO.getForecastNo())
            );
            if (count > 0) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "预测单编号已存在");
            }
            order.setForecastNo(createDTO.getForecastNo());
        }
        
        order.setForecastName(createDTO.getForecastName());
        order.setForecastType(createDTO.getForecastType());
        order.setStartDate(createDTO.getStartDate());
        order.setEndDate(createDTO.getEndDate());
        order.setTotalQuantity(createDTO.getTotalQuantity());
        order.setRemark(createDTO.getRemark());
        order.setStatus(createDTO.getStatus());
        
        forecastOrderMapper.insert(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateForecastOrder(ForecastOrderUpdateDTO updateDTO) {
        ErpForecastOrder order = forecastOrderMapper.selectById(updateDTO.getId());
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        if (updateDTO.getForecastName() != null) {
            order.setForecastName(updateDTO.getForecastName());
        }
        if (updateDTO.getForecastType() != null) {
            order.setForecastType(updateDTO.getForecastType());
        }
        if (updateDTO.getStartDate() != null) {
            order.setStartDate(updateDTO.getStartDate());
        }
        if (updateDTO.getEndDate() != null) {
            order.setEndDate(updateDTO.getEndDate());
        }
        if (updateDTO.getTotalQuantity() != null) {
            order.setTotalQuantity(updateDTO.getTotalQuantity());
        }
        if (updateDTO.getRemark() != null) {
            order.setRemark(updateDTO.getRemark());
        }
        if (updateDTO.getStatus() != null) {
            order.setStatus(updateDTO.getStatus());
        }
        
        forecastOrderMapper.updateById(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteForecastOrder(Long id) {
        ErpForecastOrder order = forecastOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        forecastOrderMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateForecastOrderStatus(Long id, Integer status) {
        ErpForecastOrder order = forecastOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        order.setStatus(status);
        forecastOrderMapper.updateById(order);
    }

    private ForecastOrderVO convertToVO(ErpForecastOrder order) {
        String forecastTypeName = switch (order.getForecastType()) {
            case 1 -> "月度预测";
            case 2 -> "季度预测";
            case 3 -> "年度预测";
            default -> "未知类型";
        };
        
        String statusName = switch (order.getStatus()) {
            case 1 -> "草稿";
            case 2 -> "已确认";
            case 3 -> "已完成";
            case 4 -> "已取消";
            default -> "未知状态";
        };
        
        return ForecastOrderVO.builder()
            .id(order.getId())
            .forecastNo(order.getForecastNo())
            .forecastName(order.getForecastName())
            .forecastType(order.getForecastType())
            .forecastTypeName(forecastTypeName)
            .startDate(order.getStartDate())
            .endDate(order.getEndDate())
            .totalQuantity(order.getTotalQuantity())
            .remark(order.getRemark())
            .status(order.getStatus())
            .statusName(statusName)
            .createBy(order.getCreateBy())
            .createTime(order.getCreateTime())
            .updateBy(order.getUpdateBy())
            .updateTime(order.getUpdateTime())
            .build();
    }
}
