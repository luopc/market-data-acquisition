package com.luopc.platform.web.mds.rates.manager;

import com.luopc.platform.market.api.Currency;
import com.luopc.platform.web.mds.handler.event.CurrencyMappingEvent;
import com.luopc.platform.web.mds.jobs.mapping.dto.CurrencyMapping;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Robin
 */
@Slf4j
@Getter
@Service
public class CcyMappingRepository {

    private final Map<Currency, CurrencyMapping> currencyMappingCache = new ConcurrentHashMap<>();

    public CurrencyMapping getCurrencyMappingByCcyCode(String ccyCode) {
        Currency currency = Currency.getInstance(ccyCode);
        return currencyMappingCache.get(currency);
    }

    public void updateCurrencyMappingList(List<CurrencyMapping> currencyMappingList) {
        Optional.ofNullable(currencyMappingList).orElse(new ArrayList<>()).forEach(this::updateCurrencyMapping);
    }

    public void updateCurrencyMapping(CurrencyMapping currencyMapping) {
        currencyMappingCache.put(Currency.getInstance(currencyMapping.getCcy()), currencyMapping);
    }

    @Async
    @EventListener(value = CurrencyMappingEvent.class)
    public void handleCurrencyMappingEvent(CurrencyMappingEvent currencyMappingEvent) {
        log.info("Receive CurrencyMappingEvent message, {}", currencyMappingEvent.getChangeList().size());
        this.updateCurrencyMappingList(currencyMappingEvent.getChangeList());
    }

}
