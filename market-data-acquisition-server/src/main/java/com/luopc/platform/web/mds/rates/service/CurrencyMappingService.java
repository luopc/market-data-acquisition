package com.luopc.platform.web.mds.rates.service;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.luopc.platform.market.api.Currency;
import com.luopc.platform.web.mds.convertors.MapStructConvertor;
import com.luopc.platform.web.mds.handler.event.CurrencyMappingEvent;
import com.luopc.platform.web.mds.jobs.mapping.dto.CurrencyMapping;
import com.luopc.platform.web.mds.rates.domain.entity.CurrencyMappingDO;
import com.luopc.platform.web.mds.rates.mappers.CurrencyMappingMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.Objects;

/**
 * @author Robin
 */
@Slf4j
@Setter
@Service
@Transactional(rollbackFor = Exception.class)
public class CurrencyMappingService {

    @Resource
    private MapStructConvertor mapStructConvertor;
    @Resource
    private CurrencyMappingMapper currencyMappingMapper;

    public CurrencyMappingDO saveNewData(CurrencyMapping currencyMapping) {
        CurrencyMappingDO currencyMappingDO = new LambdaQueryChainWrapper<>(currencyMappingMapper)
                .eq(CurrencyMappingDO::getCcy, currencyMapping.getCcy())
                .eq(CurrencyMappingDO::getCountryName, currencyMapping.getCountryName())
                .one();

        if (Objects.isNull(currencyMappingDO)) {
            currencyMappingDO = mapStructConvertor.currencyMappingToDO(currencyMapping);
            Currency currency = Currency.getInstance(currencyMapping.getCcy());
            currencyMappingDO.setUpdatedTime(new Date());
            currencyMappingDO.setDelivery(!currency.isNdfFlag());
            int status = currencyMappingMapper.insert(currencyMappingDO);
        }
        return currencyMappingDO;
    }

    @Async
    @EventListener(value = CurrencyMappingEvent.class)
    public void handleCurrencyMappingEvent(CurrencyMappingEvent currencyMappingEvent) {
        log.info("Receive msg event, {}", currencyMappingEvent.getChangeList().size());
        currencyMappingEvent.getChangeList().forEach(this::saveNewData);
    }

}
