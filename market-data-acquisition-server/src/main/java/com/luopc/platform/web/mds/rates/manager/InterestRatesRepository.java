package com.luopc.platform.web.mds.rates.manager;

import cn.hutool.core.map.MapUtil;
import com.google.common.collect.Lists;
import com.luopc.platform.market.api.CcyPair;
import com.luopc.platform.market.api.Currency;
import com.luopc.platform.web.mds.handler.event.InterestRateEvent;
import com.luopc.platform.web.mds.rates.domain.entity.CcyInterestDO;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Robin
 */
@Slf4j
@Setter
@Service
public class InterestRatesRepository {

    private final Map<Currency, CcyInterestDO> interestRateCache = new ConcurrentHashMap<>();

    public CcyInterestDO getInterestRate(Currency currency) {
        return interestRateCache.get(currency);
    }

    public CcyInterestDO getInterestRate(String ccyCode) {
        return getInterestRate(Currency.getInstance(ccyCode));
    }

    private void updateInterestRate(CcyInterestDO interestRate) {
        interestRateCache.put(Currency.getInstance(interestRate.getCcy()), interestRate);
    }

    public void updateInterestRateList(List<CcyInterestDO> interestRateList) {
        Optional.ofNullable(interestRateList).orElse(Lists.newArrayList()).forEach(this::updateInterestRate);
    }

    public Map<Currency, CcyInterestDO> getAllInterestRates() {
        return MapUtil.unmodifiable(interestRateCache);
    }

    @Async
    @EventListener(value = InterestRateEvent.class)
    public void handleSpotRateEvent(InterestRateEvent interestRateEvent) {
        log.info("Receive interestRateEvent message, {}", interestRateEvent.getChangeList().size());
        this.updateInterestRateList(interestRateEvent.getChangeList());
    }

    public Set<Currency> getCurrencies() {
        return interestRateCache.keySet();
    }
}
