package com.erp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.entity.ErpWorkOrderLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WorkOrderLogMapper extends BaseMapper<ErpWorkOrderLog> {
}
