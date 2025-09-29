package com.luopc.platform.web.mds.rates.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luopc.platform.web.mds.rates.domain.entity.CcyInterestDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CcyInterestMapper  extends BaseMapper<CcyInterestDO> {

    List<CcyInterestDO> initialLoad();

}
