package com.luopc.platform.web.mds.restful.domain.vo;

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
public class MarketQuoteVO {

    private String baseCcy;
    private String quoteCcy;
    private Double buy;
    private Double sell;
    private Double middle;
    private List<Double> lastPrices;
}
