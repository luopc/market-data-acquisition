package com.luopc.platform.web.mds.util;


import com.luopc.platform.market.api.CcyPair;
import com.luopc.platform.market.api.Currency;
import com.luopc.platform.market.api.SpotRate;
import com.luopc.platform.market.tools.RateCalculator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.text.ParseException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
public class  RateCalculatorTest {

    private final static SpotRate usd_cnyRate = new SpotRate(CcyPair.getInstance("USD", "CNY"), BigDecimal.valueOf(6.9951));
    private final static SpotRate cny_usdRate = new SpotRate(CcyPair.getInstance("CNY", "USD"), BigDecimal.valueOf(0.1413));
    private final static SpotRate eur_cnyRate = new SpotRate(CcyPair.getInstance("EUR", "CNY"), BigDecimal.valueOf(7.7944));
    private final static SpotRate cny_eurRate = new SpotRate(CcyPair.getInstance("CNY", "EUR"), BigDecimal.valueOf(0.1323));

    @Test
    void testCcy() {
        Currency priCcy = Currency.getInstance("USD");
        Currency cntCcy = Currency.getInstance("CNY");
        log.info(priCcy.toString());
        log.info(cntCcy.toString());
    }

    @Test
    void testCalculateRate() {
        double test01Rate = RateCalculator.calculateRate("USD", usd_cnyRate).setScale(4, RoundingMode.HALF_UP).doubleValue();
        assertThat(test01Rate, equalTo(Double.valueOf("6.9951")));

        double test02Rate = RateCalculator.calculateRate("CNY", usd_cnyRate).setScale(4, RoundingMode.HALF_UP).doubleValue();
        assertThat(test02Rate, equalTo(Double.valueOf("0.1430")));

        double test03Rate = RateCalculator.calculateRate("USD", cny_usdRate).setScale(4, RoundingMode.HALF_UP).doubleValue();
        assertThat(test03Rate, equalTo(Double.valueOf("7.0771")));

        double test04Rate = RateCalculator.calculateRate("CNY", cny_usdRate).setScale(4, RoundingMode.HALF_UP).doubleValue();
        assertThat(test04Rate, equalTo(Double.valueOf("0.1413")));
    }

    @Test
    void testCalculateRateConvert() {
        double test01Rate = RateCalculator.calculateRate("USD", usd_cnyRate, "EUR", eur_cnyRate).setScale(4, RoundingMode.HALF_UP).doubleValue();
        assertThat(test01Rate, equalTo(Double.valueOf("0.8975")));

        double test02Rate = RateCalculator.calculateRate("USD", usd_cnyRate, "EUR", cny_eurRate).setScale(4, RoundingMode.HALF_UP).doubleValue();
        assertThat(test02Rate, equalTo(Double.valueOf("0.9255")));

        double test03Rate = RateCalculator.calculateRate("USD", cny_usdRate, "EUR", eur_cnyRate).setScale(4, RoundingMode.HALF_UP).doubleValue();
        assertThat(test03Rate, equalTo(Double.valueOf("0.9080")));

        double test04Rate = RateCalculator.calculateRate("USD", cny_usdRate, "EUR", cny_eurRate).setScale(4, RoundingMode.HALF_UP).doubleValue();
        assertThat(test04Rate, equalTo(Double.valueOf("0.9363")));
    }


    @Test
    void testCalculateQuote() {
        {
            double se_sell_quote = RateCalculator.calculateQuote("CNY", "EUR", BigDecimal.valueOf(760.96)).doubleValue();
            log.info("CNY-EUR se_sell = {}", se_sell_quote);

            double se_buy_quote = RateCalculator.calculateQuote("CNY", "EUR", BigDecimal.valueOf(755.39)).doubleValue();
            log.info("CNY-EUR se_buy = {}", se_buy_quote);

            double cn_sell_quote = RateCalculator.calculateQuote("CNY", "EUR", BigDecimal.valueOf(763.41)).doubleValue();
            log.info("CNY-EUR cn_sell = {}", cn_sell_quote);

            double cn_buy_quote = RateCalculator.calculateQuote("CNY", "EUR", BigDecimal.valueOf(731.92)).doubleValue();
            log.info("CNY-EUR cn_buy = {}", cn_buy_quote);
        }

        {
            double se_sell_quote = RateCalculator.calculateQuote("EUR", "EUR", BigDecimal.valueOf(760.96)).doubleValue();
            log.info("EUR-CNY se_sell = {}", se_sell_quote);

            double se_buy_quote = RateCalculator.calculateQuote("EUR", "EUR", BigDecimal.valueOf(755.39)).doubleValue();
            log.info("EUR-CNY se_buy = {}", se_buy_quote);

            double cn_sell_quote = RateCalculator.calculateQuote("EUR", "EUR", BigDecimal.valueOf(763.41)).doubleValue();
            log.info("EUR-CNY cn_sell = {}", cn_sell_quote);

            double cn_buy_quote = RateCalculator.calculateQuote("EUR", "EUR", BigDecimal.valueOf(731.92)).doubleValue();
            log.info("EUR-CNY cn_buy = {}", cn_buy_quote);
        }

    }

    @Test
    void testCalculateRate_2() {
        String numStr = 538.33 + "%";
        NumberFormat numberFormat = NumberFormat.getPercentInstance(); //‰
        Number rate = null;
        try {
            rate = numberFormat.parse(numStr);
        } catch (ParseException e) {
            log.error("Cannot convert string[{}] to number.", numStr);
        }
        log.info("result = {}", rate.doubleValue());
    }

    @Test
    void testCalculateForwardRateByTenor() {
        BigDecimal forwardRate = RateCalculator.calculateForwardRateByTenor(BigDecimal.valueOf(0.8500), RateCalculator.convertPercentStr("6.5%"), RateCalculator.convertPercentStr("4.5%"), 720);
        log.info("Forward rate = {}", forwardRate.doubleValue());
        BigDecimal forwardRate2 = RateCalculator.calculateForwardRateByTenor(BigDecimal.valueOf(0.8500), RateCalculator.convertPercentStr("6.5%"), RateCalculator.convertPercentStr("4.5%"), 90);
        log.info("Forward rate2 = {}", forwardRate2.doubleValue());


    }

    @Test
    public void testRandom(){
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < 20; i++) {
            double randomDouble = secureRandom.nextDouble() * 0.009;
            log.info(String.format("%1.5f", randomDouble));
        }
    }
}
