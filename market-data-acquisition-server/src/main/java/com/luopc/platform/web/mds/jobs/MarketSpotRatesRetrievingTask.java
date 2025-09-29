package com.luopc.platform.web.mds.jobs;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.luopc.platform.market.api.CcyPair;
import com.luopc.platform.market.api.ExecutingBankEnum;
import com.luopc.platform.market.api.SpotRate;
import com.luopc.platform.market.tools.RateCalculator;
import com.luopc.platform.web.mds.convertors.MarketDataConvertor;
import com.luopc.platform.web.mds.jobs.rates.dto.market.MarketRatesMsg;
import com.luopc.platform.web.mds.jobs.rates.dto.rates.ExchangeRatesMsg;
import com.luopc.platform.web.mds.jobs.rates.service.CurrencyRateRetrievingService;
import com.luopc.platform.web.mds.jobs.rates.service.MarketRatesRetrievingService;
import com.luopc.platform.web.mds.rates.domain.dto.BankQuotation;
import com.luopc.platform.web.mds.rates.domain.entity.SpotRateDO;
import com.luopc.platform.web.mds.rates.handler.impl.BankQuotationFeedHandler;
import com.luopc.platform.web.mds.rates.handler.impl.SpotRatesFeedHandler;
import com.luopc.platform.web.mds.rates.mappers.SpotRateMapper;
import jakarta.annotation.Resource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Robin
 * @description 更新官方货币兑换汇率
 */
@Slf4j
@Setter
@Service
public class MarketSpotRatesRetrievingTask {

    @Resource
    private SpotRateMapper spotRateMapper;
    @Resource
    private SpotRatesFeedHandler spotRatesFeedHandler;
    @Resource
    private BankQuotationFeedHandler bankQuotationFeedHandler;
    @Resource
    private MarketRatesRetrievingService marketRatesRetrievingService;
    @Resource
    private CurrencyRateRetrievingService currencyRateRetrievingService;

    public void initialLoad() {
        List<SpotRateDO> spotRateListFromDataBase = spotRateMapper.initialLoad();
        List<SpotRate> spotRateList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(spotRateListFromDataBase)) {
            spotRateList = spotRateListFromDataBase.stream().map(quoteDO -> new SpotRate(quoteDO.getCcy1(), quoteDO.getCcy2(), quoteDO.getRate(), quoteDO.getUpdatedTime())).collect(Collectors.toList());
        }
        spotRatesFeedHandler.onInitialLoad(spotRateList);
    }

    /**
     * initialDelay: 初始延迟。任务的第一次执行将延迟15秒，然后将以固定间隔执行。
     * Retrieve Market Rates from
     * api.marketQuotationUrl=<a href="https://www.xe.com/api/protected/midmarket-converter/">Global Currency information</a>
     * api.marketQuotationUrl=<a href="https://zh.tradingeconomics.com/currencies">货币汇率</a>
     * api.marketQuotationUrl=<a href="https://www.chinamoney.com.cn/r/cms/www/chinamoney/data/fx/sdds-exch-rate.json">国家外汇</a>
     */
    @Async
    @Scheduled(initialDelay = 30 * 1000, fixedDelay = 60 * 60 * 1000)
    public void updateSpotRates() {
        log.info("[RetrievingMarketSpotRatesJob][定时任务每1小时执行：{}]", LocalDateTime.now());
        List<SpotRate> marketSpotRateList = Lists.newArrayList();

        //1. Global Currency information: https://www.xe.com/
        String marketRatesResult = marketRatesRetrievingService.retrieveMarketRates();
        MarketRatesMsg marketRatesMsg = JSON.parseObject(marketRatesResult, MarketRatesMsg.class);
        if (!marketRatesMsg.isSuccess()) {
            log.error("unable to RetrievingMarketRates from API, please take a look.");
            marketRatesMsg = marketRatesRetrievingService.getLocalData();
        }
        final Date lastUpdateTime = new Date(marketRatesMsg.getTimestamp());
        marketRatesMsg.getRates().forEach((ccy, rate) -> {
            marketSpotRateList.add(new SpotRate("USD", ccy.substring(0, 3), rate, lastUpdateTime));
        });

        //2. 货币汇率数据： https://zh.tradingeconomics.com/currencies
        List<SpotRate> spotRateList = currencyRateRetrievingService.getCurrencyRates();
        marketSpotRateList.addAll(spotRateList);

        //3.国家外汇数据: https://www.chinamoney.com.cn/r/cms/www/chinamoney/data/fx/sdds-exch-rate.json
        String exchangeRatesResult = currencyRateRetrievingService.retrieveExchangeRates();
        ExchangeRatesMsg exchangeRatesMsg = JSON.parseObject(exchangeRatesResult, ExchangeRatesMsg.class);
        exchangeRatesMsg.getRecords().forEach(record -> {
            if (record.getForeignCnName().length() == 6) {
                CcyPair ccyPair = CcyPair.getInstance(record.getForeignCnName());
                String price = record.getPrice();
                BigDecimal rate = new BigDecimal(price);
                if (record.getVrtName().contains("JPY")) {
                    rate = RateCalculator.div(rate, BigDecimal.valueOf(100));
                }
                marketSpotRateList.add(new SpotRate(ccyPair, rate, LocalDateTime.now()));
            }

        });

        log.info("MarketSpotRateList from API, size = {}", marketSpotRateList.size());
        spotRatesFeedHandler.onResponse(marketSpotRateList);
        mockBankQuotation(marketSpotRateList);
    }


    private void mockBankQuotation(List<SpotRate> spotRateList) {
        List<BankQuotation> bankQuotationList = new ArrayList<>();
        for (SpotRate spotRate : spotRateList) {
            for (ExecutingBankEnum bank : ExecutingBankEnum.values()) {
                bankQuotationList.add(MarketDataConvertor.convertSpotRateToBankQuote(bank, spotRate));
            }
        }
        bankQuotationFeedHandler.onResponse(bankQuotationList);
    }

}
