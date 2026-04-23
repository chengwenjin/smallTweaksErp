package com.erp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.entity.ErpForecastOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ForecastOrderMapper extends BaseMapper<ErpForecastOrder> {
}
