package com.luopc.platform.web.mds.rates.manager;

import com.google.common.collect.Lists;
import com.luopc.platform.common.core.util.SmartRandomUtil;
import com.luopc.platform.market.api.*;
import com.luopc.platform.market.api.Currency;
import com.luopc.platform.web.mds.handler.event.SpotRateEvent;
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
public class SpotRateRepository {

    private final Map<Currency, SpotRate> spotRateCacheOfCcy = new ConcurrentHashMap<>();
    private final Map<CcyPair, SpotRate> spotRateCacheOfCcyPair = new ConcurrentHashMap<>();

    public Map<CcyPair, SpotRate> getAllSpotRates() {
        return spotRateCacheOfCcyPair;
    }

    public SpotRate getSpotRate(CcyPair ccyPair) {
        return spotRateCacheOfCcyPair.get(ccyPair);
    }

    public SpotRate getSpotRate(Currency currency) {
        return spotRateCacheOfCcy.get(currency);
    }

    public void updateSpotRate(SpotRate spotRate) {
        Currency nonUsdCcy = spotRate.getCcyPair().getNonUsdCcy();
        if (Objects.nonNull(nonUsdCcy)) {
            spotRateCacheOfCcy.put(nonUsdCcy, spotRate);
        }
        spotRateCacheOfCcyPair.put(spotRate.getCcyPair(), spotRate);
    }

    public void updateSpotRateList(List<SpotRate> spotRateList) {
        Optional.ofNullable(spotRateList).orElse(Lists.newArrayList()).forEach(this::updateSpotRate);
    }

    @Async
    @EventListener(value = SpotRateEvent.class)
    public void handleSpotRateEvent(SpotRateEvent spotRateEvent) {
        log.info("Receive SpotRateEvent message, {}", spotRateEvent.getChangeList().size());
        this.updateSpotRateList(spotRateEvent.getChangeList());
    }

    public SpotRate randomGet() {
        return SmartRandomUtil.randomGet(new ArrayList<>(spotRateCacheOfCcyPair.values()));
    }

    public Set<CcyPair> getCcyPairs() {
        return spotRateCacheOfCcyPair.keySet();
    }
}
