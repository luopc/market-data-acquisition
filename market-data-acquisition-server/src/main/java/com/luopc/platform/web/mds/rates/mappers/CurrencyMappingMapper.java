package com.luopc.platform.web.mds.rates.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luopc.platform.web.mds.rates.domain.entity.CurrencyMappingDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CurrencyMappingMapper extends BaseMapper<CurrencyMappingDO> {
    List<CurrencyMappingDO> initialLoad();
}
