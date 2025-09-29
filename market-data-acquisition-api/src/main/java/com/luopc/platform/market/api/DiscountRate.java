package com.luopc.platform.market.api;

import lombok.Data;

/**
 * @author Robin
 * DiscountFactors
 */
@Data
public class DiscountRate {

    private final Currency currency;
    private final Interpolator interpolator;

}
