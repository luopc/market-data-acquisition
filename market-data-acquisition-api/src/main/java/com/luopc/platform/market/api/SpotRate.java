package com.luopc.platform.market.api;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

/**
 * @author Robin
 */
@Data
public class SpotRate implements Rate {

    private final CcyPair ccyPair;
    private final BigDecimal rate;
    private final Double rateDoubleValue;
    private LocalDateTime lastUpdateTime;

    public SpotRate(String baseCcy, String quoteCcy, BigDecimal rate) {
        this(CcyPair.getInstance(baseCcy, quoteCcy), rate, LocalDateTime.now());
    }

    public SpotRate(String baseCcy, String quoteCcy, Double rate) {
        this(CcyPair.getInstance(baseCcy, quoteCcy), BigDecimal.valueOf(rate), LocalDateTime.now());
    }

    public SpotRate(String baseCcy, String quoteCcy, BigDecimal rate, LocalDateTime lastUpdateTime) {
        this(CcyPair.getInstance(baseCcy, quoteCcy), rate, lastUpdateTime);
    }

    public SpotRate(String baseCcy, String quoteCcy, BigDecimal rate, Date updateTime) {
        this(CcyPair.getInstance(baseCcy, quoteCcy), rate);
        if (Objects.nonNull(updateTime)) {
            this.lastUpdateTime = LocalDateTime.ofInstant(updateTime.toInstant(), ZoneId.systemDefault());
        }
    }

    public SpotRate(String baseCcy, String quoteCcy, Double rate, LocalDateTime lastUpdateTime) {
        this(CcyPair.getInstance(baseCcy, quoteCcy), BigDecimal.valueOf(rate), lastUpdateTime);
    }

    public SpotRate(String baseCcy, String quoteCcy, Double rate, Date updateTime) {
        this(CcyPair.getInstance(baseCcy, quoteCcy), BigDecimal.valueOf(rate));
        if (Objects.nonNull(updateTime)) {
            this.lastUpdateTime = LocalDateTime.ofInstant(updateTime.toInstant(), ZoneId.systemDefault());
        }
    }

    public SpotRate(CcyPair ccyPair, BigDecimal rate) {
        this.ccyPair = ccyPair;
        this.rate = rate;
        this.rateDoubleValue = rate.doubleValue();
    }

    public SpotRate(CcyPair ccyPair, BigDecimal rate, LocalDateTime lastUpdateTime) {
        this(ccyPair, rate);
        this.lastUpdateTime = lastUpdateTime;
    }


}
