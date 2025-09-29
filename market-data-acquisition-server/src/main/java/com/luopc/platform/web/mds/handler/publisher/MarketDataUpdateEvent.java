package com.luopc.platform.web.mds.handler.publisher;

import com.luopc.platform.market.api.MarketPrices;
import com.luopc.platform.market.api.SpotRate;
import com.luopc.platform.web.mds.rates.domain.dto.BankQuotation;
import com.luopc.platform.web.mds.jobs.mapping.dto.CurrencyMapping;
import com.luopc.platform.web.mds.rates.domain.entity.CcyInterestDO;

import java.util.List;

/**
 * @author Robin
 */
public interface MarketDataUpdateEvent {

    /**
     * publish CurrencyMapping
     *
     * @param changeList CurrencyMapping
     */
    void publishCurrencyMapping(List<CurrencyMapping> changeList);

    /**
     * publish InterestRate
     *
     * @param changeList InterestRate
     */
    void publishInterestRate(List<CcyInterestDO> changeList);

    /**
     * publish BankQuotation
     *
     * @param changeList BankQuotation
     */
    void publishBankQuote(List<BankQuotation> changeList);

    /**
     * publish MarketQuote
     *
     * @param changeList MarketQuote
     */
    void publishMarketQuote(List<MarketPrices> changeList);

    /**
     * publish SpotRate
     *
     * @param changeList SpotRate
     */
    void publishSpotRate(List<SpotRate> changeList);
}
