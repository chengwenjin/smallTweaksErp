package com.erp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.entity.ErpWorkOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WorkOrderMapper extends BaseMapper<ErpWorkOrder> {
}
