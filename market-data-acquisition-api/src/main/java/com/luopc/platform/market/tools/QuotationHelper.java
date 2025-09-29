package com.luopc.platform.market.tools;

import com.luopc.platform.market.api.Quotation;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author by Robin
 * @className QuotationHelp
 * @description cash_sell > exchange_sell > exchange_buy > middle > cash_buy
 * @date 2024/1/6 0006 15:52
 */
public class QuotationHelper {

    public static BigDecimal getExchangeSell(Quotation quotation) {
        if (Objects.isNull(quotation.getExchangeSell()) || BigDecimal.ZERO.compareTo(BigDecimal.valueOf(quotation.getExchangeSell())) == 0) {
            return RateCalculator.multiply(getMiddle(quotation), BigDecimal.ONE.add(RateCalculator.randomDiscount()));
        } else {
            return BigDecimal.valueOf(quotation.getExchangeSell());
        }
    }

    public static BigDecimal getCashSell(Quotation quotation) {
        if (Objects.isNull(quotation.getCashSell()) || BigDecimal.ZERO.compareTo(BigDecimal.valueOf(quotation.getCashSell())) == 0) {
            return RateCalculator.multiply(getExchangeSell(quotation), BigDecimal.ONE.add(RateCalculator.randomDiscount()));
        } else {
            return BigDecimal.valueOf(quotation.getCashSell());
        }
    }

    public static BigDecimal getExchangeBuy(Quotation quotation) {
        if (Objects.isNull(quotation.getExchangeBuy()) || BigDecimal.ZERO.compareTo(BigDecimal.valueOf(quotation.getExchangeBuy())) == 0) {
            return RateCalculator.multiply(getCashBuy(quotation), BigDecimal.ONE.subtract(RateCalculator.randomDiscount()));
        } else {
            return BigDecimal.valueOf(quotation.getExchangeBuy());
        }
    }

    public static BigDecimal getCashBuy(Quotation quotation) {
        if (Objects.isNull(quotation.getCashBuy()) || BigDecimal.ZERO.compareTo(BigDecimal.valueOf(quotation.getCashBuy())) == 0) {
            return RateCalculator.multiply(getMiddle(quotation), BigDecimal.ONE.subtract(RateCalculator.randomDiscount()));
        } else {
            return BigDecimal.valueOf(quotation.getCashBuy());
        }
    }

    public static BigDecimal getMiddle(Quotation quotation) {
        if (Objects.isNull(quotation.getMiddle()) || BigDecimal.ZERO.compareTo(BigDecimal.valueOf(quotation.getMiddle())) == 0) {
            List<Double> rateList = new ArrayList<>(4);
            rateList.add(quotation.getExchangeBuy());
            rateList.add(quotation.getExchangeSell());
            rateList.add(quotation.getCashBuy());
            rateList.add(quotation.getCashSell());
            List<Double> validRateList = rateList.stream().filter(Objects::nonNull).filter(amt -> amt.compareTo(0d) != 0).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(validRateList)) {
                double middle = validRateList.stream().collect(Collectors.averagingDouble(Double::doubleValue));
                return BigDecimal.valueOf(middle).setScale(RateCalculator.RATE_SCALE, RoundingMode.HALF_UP);
            } else {
                return BigDecimal.ZERO;
            }
        } else {
            return BigDecimal.valueOf(quotation.getMiddle());
        }
    }


}
