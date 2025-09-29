package com.luopc.platform.web.mds.rates.handler;

import java.util.Collection;

/**
 * @author Robin
 */
public interface MarketDataFeedHandler<T> {

    /**
     * MarketData Initial
     *
     * @param initialList market data
     */
    void onInitialLoad(Collection<T> initialList);
    /**
     * Update marketData
     *
     * @param responseList market data
     */
    void onResponse(Collection<T> responseList);


}
