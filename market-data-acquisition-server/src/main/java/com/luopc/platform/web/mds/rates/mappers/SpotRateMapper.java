package com.luopc.platform.web.mds.rates.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luopc.platform.web.mds.rates.domain.entity.SpotRateDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Robin
 */
@Mapper
public interface SpotRateMapper extends BaseMapper<SpotRateDO> {

    List<SpotRateDO> initialLoad();
}
