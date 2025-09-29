package com.luopc.platform.web.mds.rates.handler.impl;

import com.luopc.platform.market.api.SpotRate;
import com.luopc.platform.web.mds.handler.publisher.MarketDataUpdateEventPublisher;
import com.luopc.platform.web.mds.rates.handler.AbstractMarketDataFeedHandler;
import com.luopc.platform.web.mds.rates.handler.MarketDataFeedHandler;
import com.luopc.platform.web.mds.rates.manager.SpotRateRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Robin
 */
@Slf4j
@Component
public class SpotRatesFeedHandler extends AbstractMarketDataFeedHandler<SpotRate> implements MarketDataFeedHandler<SpotRate> {

    @Resource
    private SpotRateRepository spotRateRepository;
    @Resource
    private MarketDataUpdateEventPublisher marketDataUpdateEventPublisher;

    @Override
    protected void processInitialLoadMsg(Collection<SpotRate> initialList) {
        spotRateRepository.updateSpotRateList(new ArrayList<>(initialList));
    }

    @Override
    protected void processMessage(Collection<SpotRate> entityList) {
        if (CollectionUtils.isNotEmpty(entityList)) {
            marketDataUpdateEventPublisher.publishSpotRate(new ArrayList<>(entityList));
        }
    }

}
