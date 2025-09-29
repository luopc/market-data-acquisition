package com.luopc.platform.web.mds.handler.publisher;

import com.luopc.platform.market.api.MarketPrices;
import com.luopc.platform.market.api.SpotRate;
import com.luopc.platform.web.mds.handler.event.*;
import com.luopc.platform.web.mds.rates.domain.dto.BankQuotation;
import com.luopc.platform.web.mds.jobs.mapping.dto.CurrencyMapping;
import com.luopc.platform.web.mds.rates.domain.entity.CcyInterestDO;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Robin
 */
@Component
public class MarketDataUpdateEventPublisher implements MarketDataUpdateEvent, ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(@NotNull ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publishCurrencyMapping(List<CurrencyMapping> changeList) {
        applicationEventPublisher.publishEvent(new CurrencyMappingEvent(this, changeList));
    }

    @Override
    public void publishBankQuote(List<BankQuotation> changeList) {
        applicationEventPublisher.publishEvent(new BankQuotationEvent(this, changeList));
    }

    @Override
    public void publishMarketQuote(List<MarketPrices> changeList) {
        applicationEventPublisher.publishEvent(new MarketPricesEvent(this, changeList));
    }

    @Override
    public void publishSpotRate(List<SpotRate> changeList) {
        applicationEventPublisher.publishEvent(new SpotRateEvent(this, changeList));
    }

    @Override
    public void publishInterestRate(List<CcyInterestDO> changeList) {
        applicationEventPublisher.publishEvent(new InterestRateEvent(this, changeList));
    }

}
