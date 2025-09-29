package com.luopc.platform.market.tools;

import com.alibaba.fastjson2.JSON;
import com.luopc.platform.market.api.*;
import com.luopc.platform.market.holiday.HolidayRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Robin
 */
@Slf4j
public class RateFormatter {

    public static final String COMMA_STR = ",";
    public static final String TENORS_START = "tenors=";
    public static final String RATE_START = "rates=";
    public static final String PRICES_START = "lastPrices:";

    public static String formatForwardRate(ForwardRate forwardRate) {
        if (Objects.nonNull(forwardRate)) {
            StringJoiner tenors = new StringJoiner(COMMA_STR);
            forwardRate.getInterpolatorMap().values().forEach(interpolator -> {
                tenors.add(String.format("%s:%.4f", interpolator.getTenor().getCode(), interpolator.getPoint()));
            });
            return String.format("%s, tenors=%s, fromFactor=%d", forwardRate.getCcyPair().getCcyPairStr(), tenors, ForwardRate.FROM_FACTOR);
        }
        return "";
    }

    public static ForwardRate parseForwardRate(String forwardRate) {
        if (StringUtils.isNoneBlank(forwardRate) && forwardRate.indexOf(COMMA_STR) > 0) {
            String[] strs = forwardRate.split(COMMA_STR);
            String ccyPairStr = strs[0];
            if (forwardRate.indexOf(TENORS_START) > 0) {
                String tenors = forwardRate.substring(forwardRate.indexOf(TENORS_START) + TENORS_START.length(), forwardRate.indexOf(", fromFactor") > 0 ? forwardRate.indexOf(", fromFactor") : forwardRate.length());
                String fromFactor = forwardRate.substring(forwardRate.indexOf("fromFactor=") + "fromFactor=".length());
                Map<String, String> tenorsMap = parseRateString(tenors);
                List<Interpolator> interpolatorList = new ArrayList<>(tenorsMap.size());
                tenorsMap.forEach((k, v) -> {
                    Tenor tenor = Tenor.getByCode(k);
                    //Double point = new BigDecimal(v).divide(new BigDecimal(fromFactor), 10, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(ForwardRate.FROM_FACTOR)).doubleValue();
                    Double point = new BigDecimal(v).doubleValue();
                    Interpolator interpolator = new Interpolator(HolidayRepository.getNextNearBusinessDate(LocalDate.now()), tenor, point);
                    interpolatorList.add(interpolator);
                });
                CcyPair ccyPair = CcyPair.getInstance(ccyPairStr);
                return new ForwardRate(ccyPair, Math.max(ccyPair.getCcy1().getAmtDecimal(), ccyPair.getCcy2().getAmtDecimal()), interpolatorList, LocalDateTime.now());
            }

        }
        return null;
    }

    public static String formatSpotRate(SpotRate spotRate) {
        if (Objects.nonNull(spotRate)) {
            return String.format("%s, rate=%.6f", spotRate.getCcyPair().getCcyPairStr(), spotRate.getRate());
        }
        return "";
    }

    public static SpotRate parseSpotRate(String spotRate) {
        if (StringUtils.isNoneBlank(spotRate) && spotRate.indexOf(COMMA_STR) > 0) {
            String[] strs = spotRate.split(COMMA_STR);
            String ccyPairStr = strs[0];
            String[] rateStr = strs[1].split("=");
            return new SpotRate(CcyPair.getInstance(ccyPairStr), new BigDecimal(rateStr[1]), LocalDateTime.now());
        }
        return null;
    }

    public static String formatBankQuote(String bankCode, Quotation quotation) {
        if (Objects.nonNull(quotation)) {
            return String.format("%s, %s, rates=e_buy:%.6f,e_sell:%.6f,c_buy:%.6f,c_sell:%.6f,middle:%.6f",
                    CcyPair.getInstance(quotation.getBaseCcy(), quotation.getQuoteCcy()).getCcyPairStr(), bankCode,
                    QuotationHelper.getExchangeBuy(quotation),
                    QuotationHelper.getExchangeSell(quotation),
                    QuotationHelper.getCashBuy(quotation),
                    QuotationHelper.getCashSell(quotation),
                    QuotationHelper.getMiddle(quotation));
        }
        return "";
    }

    public static Map<String, Quotation> parseBankQuote(String bankQuotation) {
        if (StringUtils.isNoneBlank(bankQuotation) && bankQuotation.indexOf(COMMA_STR) > 0) {
            Map<String, Quotation> quotationMap = new HashMap<>();
            String[] strs = bankQuotation.split(COMMA_STR);
            String ccyPairStr = strs[0].trim();
            String bankCode = strs[1].trim();
            if (bankQuotation.indexOf(RATE_START) > 0) {
                String rateStr = bankQuotation.substring(bankQuotation.indexOf(RATE_START) + RATE_START.length());

                CcyPair ccyPair = CcyPair.getInstance(ccyPairStr);
                Map<String, String> quotesMap = parseRateString(rateStr);
                Quotation quotation = new Quotation();
                quotation.setBaseCcy(ccyPair.getCcy1().getCcyCode());
                quotation.setQuoteCcy(ccyPair.getCcy2().getCcyCode());
                quotation.setUpdateTime(LocalDateTime.now());

                String exchangeBuy = quotesMap.get("e_buy");
                if (StringUtils.isNoneBlank(exchangeBuy)) {
                    quotation.setExchangeBuy(Double.valueOf(exchangeBuy));
                }
                String exchangeSell = quotesMap.get("e_sell");
                if (StringUtils.isNoneBlank(exchangeSell)) {
                    quotation.setExchangeSell(Double.valueOf(exchangeSell));
                }
                String cashBuy = quotesMap.get("c_buy");
                if (StringUtils.isNoneBlank(cashBuy)) {
                    quotation.setCashBuy(Double.valueOf(cashBuy));
                }
                String cashSell = quotesMap.get("c_sell");
                if (StringUtils.isNoneBlank(cashSell)) {
                    quotation.setCashSell(Double.valueOf(cashSell));
                }
                String middle = quotesMap.get("middle");
                if (StringUtils.isNoneBlank(middle)) {
                    quotation.setMiddle(Double.valueOf(middle));
                }
                quotationMap.put(bankCode, quotation);
                return quotationMap;
            }
        }
        return null;
    }

    public static String formatMarketPrices(MarketPrices marketPrices) {
        if (Objects.nonNull(marketPrices)) {
            return String.format("%s, rates=buy:%.6f,sell:%.6f,lastPrices:%s", marketPrices.getCcyPair().getCcyPairStr(),
                    marketPrices.getBuy(), marketPrices.getSell(), marketPrices.getLastPrices().toString());
        }
        return "";
    }


    public static MarketPrices parseMarketPrices(String marketPrices) {
        if (StringUtils.isNoneBlank(marketPrices) && marketPrices.indexOf(COMMA_STR) > 0) {

            String[] strs = marketPrices.split(COMMA_STR);
            String ccyPairStr = strs[0];
            CcyPair ccyPair = CcyPair.getInstance(ccyPairStr);
            MarketPrices marketQuote = new MarketPrices();
            marketQuote.setCcyPair(ccyPair);
            if (marketPrices.indexOf(RATE_START) > 0) {
                String pricesStr = marketPrices.substring(marketPrices.indexOf(RATE_START) + RATE_START.length(), marketPrices.indexOf(PRICES_START) > 0 ? (marketPrices.indexOf(PRICES_START) - 1) : marketPrices.length());
                Map<String, String> quotesMap = parseRateString(pricesStr);

                String buy = quotesMap.get("buy");
                if (StringUtils.isNoneBlank(buy)) {
                    marketQuote.setBuy(Double.valueOf(buy));
                }
                String sell = quotesMap.get("sell");
                if (StringUtils.isNoneBlank(sell)) {
                    marketQuote.setSell(Double.valueOf(sell));
                }
                if (marketPrices.indexOf(PRICES_START) > 0) {
                    String lastPrices = marketPrices.substring(marketPrices.indexOf(PRICES_START) + PRICES_START.length());
                    marketQuote.setLastPrices(JSON.parseArray(lastPrices, Double.class));
                }
            }
            return marketQuote;
        }
        return null;
    }

    private static Map<String, String> parseRateString(String rateStr) {
        //e_buy:%.6f,e_sell:%.6f,c_buy:%.6f,c_sell:%.6f,middle:%.6f
        //TDY:0.0000,TOM:-0.2100,SPOT:-0.4300,1W:-1.5000,2W:-2.9900,1M:-6.6100,2M:-12.7600,3M:-19.2500,6M:-37.9800,18M:-108.2600,1Y:-74.3400,2Y:-141.0300,3Y:-201.3400,5Y:-256.1600,10Y:-502.8400,15Y:-639.6500
        Map<String, String> rateMap = new HashMap<>();
        if (StringUtils.isNoneBlank(rateStr)) {
            String[] rateSplit = rateStr.split(COMMA_STR);
            for (String rate : rateSplit) {
                String[] rateDetail = rate.split(":");
                rateMap.put(rateDetail[0], rateDetail[1]);
            }
        }
        return rateMap;
    }

}
