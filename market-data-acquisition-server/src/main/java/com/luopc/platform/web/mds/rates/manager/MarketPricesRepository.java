package com.luopc.platform.web.mds.rates.manager;

import cn.hutool.core.map.MapUtil;
import com.google.common.collect.Lists;
import com.luopc.platform.common.core.util.SmartRandomUtil;
import com.luopc.platform.market.api.CcyPair;
import com.luopc.platform.market.api.MarketPrices;
import com.luopc.platform.web.mds.handler.event.MarketPricesEvent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Robin
 */
@Slf4j
@Setter
@Service
public class MarketPricesRepository {

    private final Map<CcyPair, com.luopc.platform.market.api.MarketPrices> marketQuotesMap = new ConcurrentHashMap<>();

    public MarketPrices getMarketPrices(CcyPair ccyPair) {
        return marketQuotesMap.get(ccyPair);
    }

    public com.luopc.platform.market.api.MarketPrices getMarketPrices(String ccyPair) {
        return getMarketPrices(CcyPair.getInstance(ccyPair));
    }

    public void updateMarketPrices(com.luopc.platform.market.api.MarketPrices marketPrices) {
        marketQuotesMap.put(marketPrices.getCcyPair(), marketPrices);
    }

    public void updateMarketPricesList(List<com.luopc.platform.market.api.MarketPrices> marketPricesList) {
        Optional.ofNullable(marketPricesList).orElse(Lists.newArrayList()).forEach(this::updateMarketPrices);
    }

    public Map<CcyPair, com.luopc.platform.market.api.MarketPrices> getAllMarketQuotes() {
        return MapUtil.unmodifiable(marketQuotesMap);
    }

    @Async
    @EventListener(value = MarketPricesEvent.class)
    public void handleMarketQuotationEvent(MarketPricesEvent marketPricesEvent) {
        log.info("Receive MarketQuotationEvent message, {}", marketPricesEvent.getChangeList().size());
        this.updateMarketPricesList(marketPricesEvent.getChangeList());
    }

    public MarketPrices randomGet() {
        return SmartRandomUtil.randomGet(new ArrayList<>(marketQuotesMap.values()));
    }

    public Set<CcyPair> getCcyPairs() {
        return marketQuotesMap.keySet();
    }
}
