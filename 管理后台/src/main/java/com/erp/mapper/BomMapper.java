package com.erp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.entity.ErpBom;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BomMapper extends BaseMapper<ErpBom> {
}
