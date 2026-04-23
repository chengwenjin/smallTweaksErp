package com.erp.service;

import com.baserbac.common.enums.ResultCode;
import com.baserbac.common.exception.BusinessException;
import com.baserbac.common.result.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.dto.SalesOrderCreateDTO;
import com.erp.dto.SalesOrderQueryDTO;
import com.erp.dto.SalesOrderUpdateDTO;
import com.erp.entity.ErpSalesOrder;
import com.erp.mapper.SalesOrderMapper;
import com.erp.vo.SalesOrderVO;
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
public class SalesOrderService {

    private final SalesOrderMapper salesOrderMapper;

    public PageResult<SalesOrderVO> pageSalesOrders(SalesOrderQueryDTO queryDTO) {
        Page<ErpSalesOrder> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<ErpSalesOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(queryDTO.getOrderNo() != null, ErpSalesOrder::getOrderNo, queryDTO.getOrderNo())
               .like(queryDTO.getCustomerName() != null, ErpSalesOrder::getCustomerName, queryDTO.getCustomerName())
               .eq(queryDTO.getOrderType() != null, ErpSalesOrder::getOrderType, queryDTO.getOrderType())
               .eq(queryDTO.getStatus() != null, ErpSalesOrder::getStatus, queryDTO.getStatus())
               .ge(queryDTO.getStartDate() != null, ErpSalesOrder::getOrderDate, queryDTO.getStartDate())
               .le(queryDTO.getEndDate() != null, ErpSalesOrder::getOrderDate, queryDTO.getEndDate())
               .orderByDesc(ErpSalesOrder::getCreateTime);
        
        Page<ErpSalesOrder> result = salesOrderMapper.selectPage(page, wrapper);
        
        List<SalesOrderVO> voList = result.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        return PageResult.of(
            result.getTotal(), 
            voList, 
            (long) result.getCurrent(), 
            (long) result.getSize()
        );
    }

    public SalesOrderVO getSalesOrderById(Long id) {
        ErpSalesOrder order = salesOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return convertToVO(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createSalesOrder(SalesOrderCreateDTO createDTO) {
        ErpSalesOrder order = new ErpSalesOrder();
        
        if (createDTO.getOrderNo() == null || createDTO.getOrderNo().isEmpty()) {
            String orderNo = "SO" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + 
                String.format("%04d", System.currentTimeMillis() % 10000);
            order.setOrderNo(orderNo);
        } else {
            Long count = salesOrderMapper.selectCount(
                new LambdaQueryWrapper<ErpSalesOrder>()
                    .eq(ErpSalesOrder::getOrderNo, createDTO.getOrderNo())
            );
            if (count > 0) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "订单编号已存在");
            }
            order.setOrderNo(createDTO.getOrderNo());
        }
        
        order.setCustomerName(createDTO.getCustomerName());
        order.setCustomerCode(createDTO.getCustomerCode());
        order.setOrderType(createDTO.getOrderType());
        order.setOrderDate(createDTO.getOrderDate() != null ? createDTO.getOrderDate() : LocalDate.now());
        order.setDeliveryDate(createDTO.getDeliveryDate());
        order.setTotalAmount(createDTO.getTotalAmount());
        order.setRemark(createDTO.getRemark());
        order.setStatus(createDTO.getStatus());
        
        salesOrderMapper.insert(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateSalesOrder(SalesOrderUpdateDTO updateDTO) {
        ErpSalesOrder order = salesOrderMapper.selectById(updateDTO.getId());
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        if (updateDTO.getCustomerName() != null) {
            order.setCustomerName(updateDTO.getCustomerName());
        }
        if (updateDTO.getCustomerCode() != null) {
            order.setCustomerCode(updateDTO.getCustomerCode());
        }
        if (updateDTO.getOrderType() != null) {
            order.setOrderType(updateDTO.getOrderType());
        }
        if (updateDTO.getOrderDate() != null) {
            order.setOrderDate(updateDTO.getOrderDate());
        }
        if (updateDTO.getDeliveryDate() != null) {
            order.setDeliveryDate(updateDTO.getDeliveryDate());
        }
        if (updateDTO.getTotalAmount() != null) {
            order.setTotalAmount(updateDTO.getTotalAmount());
        }
        if (updateDTO.getRemark() != null) {
            order.setRemark(updateDTO.getRemark());
        }
        if (updateDTO.getStatus() != null) {
            order.setStatus(updateDTO.getStatus());
        }
        
        salesOrderMapper.updateById(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteSalesOrder(Long id) {
        ErpSalesOrder order = salesOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        salesOrderMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateSalesOrderStatus(Long id, Integer status) {
        ErpSalesOrder order = salesOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        order.setStatus(status);
        salesOrderMapper.updateById(order);
    }

    private SalesOrderVO convertToVO(ErpSalesOrder order) {
        String orderTypeName = switch (order.getOrderType()) {
            case 1 -> "普通订单";
            case 2 -> "紧急订单";
            default -> "未知类型";
        };
        
        String statusName = switch (order.getStatus()) {
            case 1 -> "待确认";
            case 2 -> "已确认";
            case 3 -> "已发货";
            case 4 -> "已完成";
            case 5 -> "已取消";
            default -> "未知状态";
        };
        
        return SalesOrderVO.builder()
            .id(order.getId())
            .orderNo(order.getOrderNo())
            .customerName(order.getCustomerName())
            .customerCode(order.getCustomerCode())
            .orderType(order.getOrderType())
            .orderTypeName(orderTypeName)
            .orderDate(order.getOrderDate())
            .deliveryDate(order.getDeliveryDate())
            .totalAmount(order.getTotalAmount())
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
