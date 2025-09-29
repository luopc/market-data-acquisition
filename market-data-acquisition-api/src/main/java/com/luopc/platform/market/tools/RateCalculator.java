package com.luopc.platform.market.tools;

import com.luopc.platform.common.core.util.SmartNumberUtil;
import com.luopc.platform.market.api.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Robin
 */
@Slf4j
public class RateCalculator {
    public static final int RATE_SCALE = 6;

    public static BigDecimal toUsd(Currency ccy, BigDecimal ccyAmt, SpotRate spotRateWithUsd) {
        if (ccy.isUsdFlag()) {
            return ccyAmt;
        } else {
            CcyPair ccyPair = spotRateWithUsd.getCcyPair();
            if (ccyPair.getCcy1().isUsdFlag()) {
                return div(ccyAmt, spotRateWithUsd.getRate(), ccyPair.getCcy2().getAmtDecimal());
            } else {
                return multiply(ccyAmt, spotRateWithUsd.getRate(), ccyPair.getCcy1().getAmtDecimal());
            }
        }
    }

    /**
     * 换汇汇率=即期外汇价格X(报价币利率一被报价币利率X天数/360)
     * (1)报价币利率大于被报价币利率，其利率差为正数，此时远期汇率减其即期汇率大于零，称之为升水。
     * (2)报价币利率小于被报价币利率，其利率差为负数，此时远期汇率减其即期汇率小于零，称之为贴水。
     * 1. 即期汇率EUR/USD为0.8500
     * 2. 6个月美元利率为6.5%
     * 3. 6个月欧元利率为4.5%
     * 4. 卖出6个月远期外汇之价格=0.85X(1+6.5%X180/360)/(1+4.5%X180/360)=xxx
     *
     * @param rate
     * @param baseCcyInterestRate
     * @param tenorDays
     * @return forwardRate
     * <p>
     * 问题如下：The JPY/AUD spot exchange rate is 82.42, the JPY interest rate is 0.15%, and the AUD interest rate is 4.95%.
     * If the interest rates are quoted on the basis of a 360-day year, the 90-day forward points in JPY/AUD would be closest to:
     * 选项：A.–377.0   B. –97.7   C. 98.9.
     * <p>
     * 解释：B is correct.
     * 1. The forward exchange rate is given by
     * JPY/AUD=S(JPY/AUD)(1+ijpy)/(1+iAUD)=82.42*[(1+0.0015(90/360)]/[1+0.0495(90/360)]=82.42*0.98815=81.443
     * 2.  The forward points are
     * 100 × (F – S) = 100 × (81.443 – 82.42) = 100 × (–0.977) = –97.7
     * Note that because the spot exchange rate is quoted with two decimal places, the forward points are scaled by 100.
     */
    public static BigDecimal calculateForwardRateByTenor(BigDecimal rate, BigDecimal baseCcyInterestRate, BigDecimal quoteCcyInterestRate, int tenorDays) {
        //1+6.5%X180/360
        BigDecimal baseCcyByTenor = BigDecimal.ONE.add(div(multiply(baseCcyInterestRate, BigDecimal.valueOf(tenorDays)), BigDecimal.valueOf(360)));
        //(1+4.5%X180/360)
        BigDecimal quoteCcyByTenor = BigDecimal.ONE.add(div(multiply(quoteCcyInterestRate, BigDecimal.valueOf(tenorDays)), BigDecimal.valueOf(360)));
        //0.85X(1+6.5%X180/360)/(1+4.5%X180/360)
        return multiply(rate, div(baseCcyByTenor, quoteCcyByTenor));
    }

    public static Double calculateForwardRateByTenor(double rate, double baseCcyInterestRate, double quoteCcyInterestRate, int tenorDays) {
        return calculateForwardRateByTenor(BigDecimal.valueOf(rate), BigDecimal.valueOf(baseCcyInterestRate), BigDecimal.valueOf(quoteCcyInterestRate), tenorDays).doubleValue();
    }

    /**
     * Formula = rate * [(baseCcyInterestRate - quoteCcyInterestRate) * days/360] - rate =
     *
     * @param rate
     * @param baseCcyInterestRate
     * @param quoteCcyInterestRate
     * @return
     */
    public static Map<Tenor, Double> calculateForwardPoints(Double rate, Double baseCcyInterestRate, Double quoteCcyInterestRate) {
        Map<Tenor, Double> forwardPointCurves = new TreeMap<>();
        for (Tenor tenor : Tenor.values()) {
            double forwardPoint = calculateForwardRateByTenor(rate, baseCcyInterestRate, quoteCcyInterestRate, tenor.getDays()) - rate;
            forwardPointCurves.put(tenor, (forwardPoint * ForwardRate.FROM_FACTOR));
        }
        return forwardPointCurves;
    }

    public static Map<Tenor, BigDecimal> calculateForwardPoints(BigDecimal rate, BigDecimal baseCcyInterestRate, BigDecimal quoteCcyInterestRate) {
        Map<Tenor, BigDecimal> forwardPointCurves = new TreeMap<>();
        for (Tenor tenor : Tenor.values()) {
            BigDecimal forwardPoint = calculateForwardRateByTenor(rate, baseCcyInterestRate, quoteCcyInterestRate, tenor.getDays()).subtract(rate);
            forwardPointCurves.put(tenor, forwardPoint.multiply(BigDecimal.valueOf(ForwardRate.FROM_FACTOR)));
        }
        return forwardPointCurves;
    }


    public static Double calculateForwardPoints(Double rate, Double baseCcyInterestRate, Double quoteCcyInterestRate, int tenorDays) {
        return calculateForwardPoints(BigDecimal.valueOf(rate), BigDecimal.valueOf(baseCcyInterestRate), BigDecimal.valueOf(quoteCcyInterestRate), tenorDays).doubleValue();
    }

    public static BigDecimal calculateForwardPoints(BigDecimal rate, BigDecimal baseCcyInterestRate, BigDecimal quoteCcyInterestRate, int tenorDays) {
        BigDecimal forwardPoint = calculateForwardRateByTenor(rate, baseCcyInterestRate, quoteCcyInterestRate, tenorDays).subtract(rate);
        return forwardPoint.multiply(BigDecimal.valueOf(ForwardRate.FROM_FACTOR));
    }

    public static Double calculateRateInDouble(String ccy, SpotRate ccyPairRate) {
        return calculateRate(ccy, ccyPairRate).doubleValue();
    }

    public static BigDecimal calculateRate(String ccy, SpotRate ccyPairRate) {
        if (ccyPairRate != null) {
            if (ccyPairRate.getCcyPair().getCcy1().getCcyCode().equalsIgnoreCase(ccy)) {
                return ccyPairRate.getRate().setScale(RATE_SCALE, RoundingMode.HALF_UP);
            } else {
                return RateCalculator.div(BigDecimal.ONE, ccyPairRate.getRate(), RATE_SCALE);
            }
        }
        return BigDecimal.ONE;
    }


    public static Double calculateRateInDouble(String ccy1, SpotRate ccyPairRate1, String ccy2, SpotRate ccyPairRate2) {
        return calculateRate(ccy1, ccyPairRate1, ccy2, ccyPairRate2).doubleValue();
    }

    public static BigDecimal calculateRate(String ccy1, SpotRate ccyPairRate1, String ccy2, SpotRate ccyPairRate2) {
        if (ccyPairRate1 != null && ccyPairRate2 != null) {
            if (ccyPairRate1.getCcyPair().getCcy1().getCcyCode().equalsIgnoreCase(ccy1)) {
                if (ccyPairRate2.getCcyPair().getCcy2().getCcyCode().equalsIgnoreCase(ccy2)) {
                    return multiply(ccyPairRate1.getRate(), ccyPairRate2.getRate(), RATE_SCALE);

                } else {
                    return div(ccyPairRate1.getRate(), ccyPairRate2.getRate(), RATE_SCALE);
                }
            } else {
                if (ccyPairRate2.getCcyPair().getCcy2().getCcyCode().equalsIgnoreCase(ccy2)) {
                    return RateCalculator.div(ccyPairRate2.getRate(), ccyPairRate1.getRate(), RATE_SCALE);
                } else {
                    BigDecimal calRate = RateCalculator.multiply(ccyPairRate1.getRate(), ccyPairRate2.getRate(), RATE_SCALE);
                    return RateCalculator.div(BigDecimal.ONE, calRate, RATE_SCALE);
                }
            }
        }
        return BigDecimal.ONE;
    }

    public static Double calculateQuoteInDouble(String priCcy, String seCcy, BigDecimal quote) {
        return calculateQuote(priCcy, seCcy, quote).doubleValue();
    }

    public static BigDecimal calculateQuote(String priCcy, String seCcy, BigDecimal quote) {
        if (priCcy.equalsIgnoreCase(seCcy)) {
            return div(quote, BigDecimal.valueOf(100), RATE_SCALE);
        } else {
            return div(BigDecimal.ONE, div(quote, BigDecimal.valueOf(100), RATE_SCALE), RATE_SCALE);
        }
    }

    public static Double div(Double v1, Double v2) {
        return div(v1, v2, RATE_SCALE);
    }

    public static Double div(Double v1, Double v2, int scale) {
        return div(BigDecimal.valueOf(v1), BigDecimal.valueOf(v2), scale).doubleValue();
    }

    public static Double multiply(Double v1, Double v2) {
        return multiply(v1, v2, RATE_SCALE);
    }

    public static Double multiply(Double v1, Double v2, int scale) {
        return multiply(BigDecimal.valueOf(v1), BigDecimal.valueOf(v2), scale).doubleValue();
    }

    public static Double sum(Double v1, Double v2, int scale) {
        return sum(BigDecimal.valueOf(v1), BigDecimal.valueOf(v2), scale).doubleValue();
    }

    public static Double sum(Double v1, Double v2) {
        return sum(BigDecimal.valueOf(v1), BigDecimal.valueOf(v2)).doubleValue();
    }

    public static Double randomDiscountInDouble() {
        return randomDiscount().doubleValue();
    }

    public static Double randomDiscountInDouble(double maxDiscount) {
        return randomDiscount(maxDiscount).doubleValue();
    }

    /**
     * double/double
     *
     * @param v1    乘数
     * @param v2    乘数
     * @param scale
     * @return result
     */
    public static BigDecimal multiply(BigDecimal v1, BigDecimal v2, int scale) {
        //ROUND_HALF_UP:四舍五入
        return v1.multiply(v2).setScale(scale, RoundingMode.HALF_UP);
    }


    public static BigDecimal multiply(BigDecimal v1, BigDecimal v2) {
        return multiply(v1, v2, RATE_SCALE);
    }

    public static BigDecimal div(BigDecimal v1, BigDecimal v2) {
        return div(v1, v2, RATE_SCALE);
    }

    /**
     * double/double
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale
     * @return result
     */
    public static BigDecimal div(BigDecimal v1, BigDecimal v2, int scale) {
        if (BigDecimal.ZERO.compareTo(v2) != 0) {
            //ROUND_HALF_UP:四舍五入
            return v1.divide(v2, scale, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public static BigDecimal convertPercentStr(String numStr) {
        if (numStr.contains("%")) {
            //‰
            NumberFormat numberFormat = NumberFormat.getPercentInstance();
            Number rate = null;
            try {
                rate = numberFormat.parse(numStr);
                return multiply(BigDecimal.valueOf(rate.doubleValue()), BigDecimal.ONE);
            } catch (ParseException e) {
                log.error("Cannot convert string[{}] to number.", numStr);
            }
        } else if (numStr.contains("‰")) {
            BigDecimal num = BigDecimal.valueOf(Double.parseDouble(numStr.replace("‰", "")));
            return div(num, BigDecimal.valueOf(1000D), RATE_SCALE);
        } else if (NumberUtils.isDigits(numStr)) {
            return multiply(BigDecimal.valueOf(Double.parseDouble(numStr)), BigDecimal.ONE);
        }
        return BigDecimal.ZERO;
    }

    public static BigDecimal sum(BigDecimal v1, BigDecimal v2, int scale) {
        //ROUND_HALF_UP:四舍五入
        return v1.add(v2).setScale(scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal sum(BigDecimal v1, BigDecimal v2) {
        //ROUND_HALF_UP:四舍五入
        return v1.add(v2).setScale(RATE_SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal randomDiscount() {
        return randomDiscount(0.003);
    }

    public static BigDecimal randomDiscount(double maxDiscount) {
        Double randomValue = RateCalculator.multiply(SmartNumberUtil.randomDouble(RateCalculator.RATE_SCALE, 1, 99), 0.01);
        return BigDecimal.valueOf(RateCalculator.multiply(randomValue, maxDiscount));
    }

    public static BigDecimal generateAmt() {
        return BigDecimal.valueOf(SmartNumberUtil.randomNumber(5, 8000) * 1000L);
    }

}
