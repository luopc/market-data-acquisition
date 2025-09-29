package com.luopc.platform.market.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Robin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketPrices {

    private CcyPair ccyPair;
    private Double buy;
    private Double sell;
    private List<Double> lastPrices;

    public MarketPrices(CcyPair ccyPair, Double buy, Double sell) {
        this.ccyPair = ccyPair;
        this.buy = buy;
        this.sell = sell;
    }
}
