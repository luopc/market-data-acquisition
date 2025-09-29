package com.luopc.platform.web.mds.jobs.rates.dto.interest;

import com.luopc.platform.market.tools.RateCalculator;
import lombok.Data;

/**
 * @author Robin
 */
@Data
public class InterestRate {

    private String countryName;
    private String ccy;
    private Double rate;
    private Double preRate;
    private String updateDate;
    private String unit;

    public double getRealRate() {
        if (rate > 0) {
            return RateCalculator.convertPercentStr(rate + unit).doubleValue();
        }
        return 0D;
    }

    public double getRealPreRate() {
        if (preRate > 0) {
            return RateCalculator.convertPercentStr(preRate + unit).doubleValue();
        }
        return 0D;
    }
}
