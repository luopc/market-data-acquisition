package com.luopc.platform.web.mds.jobs.rates.service;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.luopc.platform.market.api.CcyPair;
import com.luopc.platform.market.api.SpotRate;
import com.luopc.platform.market.tools.RateCalculator;
import com.luopc.platform.web.mds.config.EconomicsApiConfig;
import com.luopc.platform.web.mds.jobs.rates.dto.market.MarketRatesMsg;
import com.luopc.platform.web.mds.jobs.rates.dto.rates.ExchangeRatesMsg;
import jakarta.annotation.Resource;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author by Robin
 * @className CurrencyRatesRetrieveService
 * @description 国家货币汇率数据
 * @date 2024/1/6 0006 10:44
 */
@Slf4j
@Setter
@Service
@NoArgsConstructor
public class MarketRatesRetrievingService {

    @Resource
    protected EconomicsApiConfig economicsApiConfig;
    @Resource
    private CurrencyRateRetrievingService currencyRateRetrievingService;


    public List<SpotRate> getAggregatedSpotRates() {
        log.info("[RetrievingMarketSpotRatesJob][定时任务每1小时执行：{}]", LocalDateTime.now());
        List<SpotRate> marketSpotRateList = Lists.newArrayList();

        //1. Global Currency information: https://www.xe.com/
        String marketRatesResult = retrieveMarketRates();
        MarketRatesMsg marketRatesMsg = JSON.parseObject(marketRatesResult, MarketRatesMsg.class);
        if (!marketRatesMsg.isSuccess()) {
            log.error("unable to RetrievingMarketRates from API, please take a look.");
            marketRatesMsg = getLocalData();
        }
        final Date lastUpdateTime = new Date(marketRatesMsg.getTimestamp());
        marketRatesMsg.getRates().forEach((ccy, rate) -> {
            marketSpotRateList.add(new SpotRate("USD", ccy.substring(0, 3), rate, lastUpdateTime));
        });
        log.info("MarketSpotRateList from API[www.xe.com], size = {}", marketSpotRateList.size());

        //2. 货币汇率数据： https://zh.tradingeconomics.com/currencies
        List<SpotRate> tradingSpotRateList = currencyRateRetrievingService.getCurrencyRates();
        log.info("MarketSpotRateList from API[zh.tradingeconomics.com], size = {}", marketSpotRateList.size());
        marketSpotRateList.addAll(tradingSpotRateList);

        //3.国家外汇数据: https://www.chinamoney.com.cn/r/cms/www/chinamoney/data/fx/sdds-exch-rate.json
        String exchangeRatesResult = currencyRateRetrievingService.retrieveExchangeRates();
        ExchangeRatesMsg exchangeRatesMsg = JSON.parseObject(exchangeRatesResult, ExchangeRatesMsg.class);
        marketSpotRateList.addAll(extractedSpotRates(exchangeRatesMsg));

        log.info("MarketSpotRateList from all active APIs, size = {}", marketSpotRateList.size());
        return marketSpotRateList;
    }

    public List<SpotRate> extractedSpotRates(ExchangeRatesMsg exchangeRatesMsg) {
        log.info("ExchangeRatesMsg size = {}", exchangeRatesMsg.getRecords().size());
        List<SpotRate> chinaSpotRateList = exchangeRatesMsg.getRecords().stream().map(record -> {
            log.info("ExchangeRatesMsg.getExchangeRates(), record = {}", record);
            if (record.getForeignCnName().length() == 7) {
                CcyPair ccyPair = CcyPair.getInstance(record.getForeignCnName());
                String price = record.getPrice();
                BigDecimal rate = new BigDecimal(price);
                if (record.getVrtName().contains("JPY")) {
                    rate = RateCalculator.div(rate, BigDecimal.valueOf(100));
                }
                return new SpotRate(ccyPair, rate, LocalDateTime.now());
            }
            return null;
        }).filter(Objects::nonNull).toList();
        log.info("MarketSpotRateList from API[www.chinamoney.com.cn], size = {}", chinaSpotRateList.size());
        return chinaSpotRateList;
    }


    public String retrieveMarketRates() {
        String url = economicsApiConfig.getMarketRatesUrl();

        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6)");
        httpGet.addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        httpGet.addHeader("Authorization", "Basic " + economicsApiConfig.getMarketRatesAuthorization());

        String resultMessage = "";
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            CloseableHttpResponse response = httpclient.execute(httpGet);
            if (HttpStatus.OK_200 == response.getCode()) {
                resultMessage = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                log.info("Retrieving data from url {}", url);
            }
        } catch (IOException e) {
            log.error("Unable to retrieve data from API server, url = {}", url);
        } catch (ParseException e) {
            log.error("Unable to parse data from API server, url = {}", url);
        }
        return resultMessage;
    }

    public MarketRatesMsg getLocalData() {
        String sourceFile = "static/marketRates-xe.json";
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(sourceFile)) {
            return JSON.parseObject(inputStream, MarketRatesMsg.class);
        } catch (IOException var4) {
            log.error("Unable to retrieve data from Local, sourceFile = {}", sourceFile, var4);
            return null;
        }
    }
}
