package com.luopc.platform.web.mds.rates.handler.impl;

import com.luopc.platform.web.mds.handler.publisher.MarketDataUpdateEventPublisher;
import com.luopc.platform.web.mds.jobs.mapping.dto.CurrencyMapping;
import com.luopc.platform.web.mds.rates.handler.AbstractMarketDataFeedHandler;
import com.luopc.platform.web.mds.rates.handler.MarketDataFeedHandler;
import com.luopc.platform.web.mds.rates.manager.CcyMappingRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Robin
 */
@Slf4j
@Component
public class CurrencyMappingFeedHandler extends AbstractMarketDataFeedHandler<CurrencyMapping> implements MarketDataFeedHandler<CurrencyMapping> {

    @Resource
    private CcyMappingRepository ccyMappingRepository;
    @Resource
    private MarketDataUpdateEventPublisher marketDataUpdateEventPublisher;

    @Override
    protected void processInitialLoadMsg(Collection<CurrencyMapping> initialList) {
        ccyMappingRepository.updateCurrencyMappingList(new ArrayList<>(initialList));
    }

    @Override
    protected void processMessage(Collection<CurrencyMapping> entityList) {
        if (CollectionUtils.isNotEmpty(entityList)) {
            marketDataUpdateEventPublisher.publishCurrencyMapping(new ArrayList<>(entityList));
        }
    }

}
