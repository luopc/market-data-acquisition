package com.luopc.platform.web.mds.rates.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luopc.platform.web.mds.rates.domain.entity.BankQuotationDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Robin
 */
@Mapper
public interface BankQuotationMapper extends BaseMapper<BankQuotationDO> {

    List<BankQuotationDO> initialLoad();
}
