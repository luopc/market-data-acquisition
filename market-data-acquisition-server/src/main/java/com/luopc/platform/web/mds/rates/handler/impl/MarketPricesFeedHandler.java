package com.luopc.platform.web.mds.rates.handler.impl;

import com.luopc.platform.market.api.MarketPrices;
import com.luopc.platform.web.mds.handler.publisher.MarketDataUpdateEventPublisher;
import com.luopc.platform.web.mds.rates.handler.AbstractMarketDataFeedHandler;
import com.luopc.platform.web.mds.rates.handler.MarketDataFeedHandler;
import com.luopc.platform.web.mds.rates.manager.MarketPricesRepository;
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
public class MarketPricesFeedHandler extends AbstractMarketDataFeedHandler<MarketPrices> implements MarketDataFeedHandler<MarketPrices> {

    @Resource
    private MarketPricesRepository marketPricesRepository;
    @Resource
    private MarketDataUpdateEventPublisher marketDataUpdateEventPublisher;

    @Override
    protected void processInitialLoadMsg(Collection<MarketPrices> initialList) {
        marketPricesRepository.updateMarketPricesList(new ArrayList<>(initialList));
    }

    @Override
    protected void processMessage(Collection<MarketPrices> entityList) {
        if (CollectionUtils.isNotEmpty(entityList)) {
            marketDataUpdateEventPublisher.publishMarketQuote(new ArrayList<>(entityList));
        }
    }

}
