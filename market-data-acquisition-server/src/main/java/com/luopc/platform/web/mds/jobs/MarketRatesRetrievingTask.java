package com.luopc.platform.web.mds.jobs;

import com.google.common.collect.Lists;
import com.luopc.platform.market.api.*;
import com.luopc.platform.market.tools.RateCalculator;
import com.luopc.platform.web.mds.jobs.rates.service.MarketPricesRetrievingService;
import com.luopc.platform.web.mds.rates.domain.entity.MarketQuotationDO;
import com.luopc.platform.web.mds.rates.handler.impl.MarketPricesFeedHandler;
import com.luopc.platform.web.mds.rates.handler.impl.SpotRatesFeedHandler;
import com.luopc.platform.web.mds.rates.mappers.MarketQuotationMapper;
import jakarta.annotation.Resource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Robin
 * @description 更新市场货币兑换汇率
 */
@Slf4j
@Setter
@Service
public class MarketRatesRetrievingTask {


    @Resource
    private SpotRatesFeedHandler spotRatesFeedHandler;
    @Resource
    private MarketQuotationMapper marketQuotationMapper;
    @Resource
    private MarketPricesFeedHandler marketPricesFeedHandler;
    @Resource
    private MarketPricesRetrievingService marketPricesRetrievingService;


    public void initialLoad() {
        List<MarketQuotationDO> marketQuotationListFromDataBase = marketQuotationMapper.initialLoad();
        List<MarketPrices> marketPricesList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(marketQuotationListFromDataBase)) {
            marketPricesList = marketQuotationListFromDataBase.stream().map(quoteDO -> {
                MarketPrices marketPrices = new MarketPrices();
                CcyPair ccyPair = CcyPair.getInstance(quoteDO.getBaseCcy(), quoteDO.getQuoteCcy());
                marketPrices.setCcyPair(ccyPair);
                marketPrices.setBuy(quoteDO.getBuy());
                marketPrices.setSell(quoteDO.getSell());
                marketPrices.setLastPrices(Arrays.stream(quoteDO.getLastPrices().split(",")).map(Double::valueOf).collect(Collectors.toList()));
                return marketPrices;
            }).collect(Collectors.toList());
        }
        marketPricesFeedHandler.onInitialLoad(marketPricesList);
    }


    /**
     * initialDelay: 初始延迟。任务的第一次执行将延迟15秒，然后将以固定间隔执行。
     * Retrieve Market Quotation from
     * api.marketQuotationUrl=https://www.xtrendspeed.com/api/quotation/symbol/list/detail
     */
    @Async
    @Scheduled(initialDelay = 15 * 1000, fixedDelay = 60 * 60 * 1000)
    public void updateMarketQuotations() {
        log.info("[RetrievingMarketQuotationJob][定时任务每1小时执行：{}]", LocalDateTime.now());
        List<MarketPrices> marketPricesList = marketPricesRetrievingService.getMarketPricesList();
        marketPricesFeedHandler.onResponse(marketPricesList);
        List<SpotRate> spotRateList = extractedSpotRateFromMarketQuote(marketPricesList);
        spotRatesFeedHandler.onResponse(spotRateList);
    }

    private List<SpotRate> extractedSpotRateFromMarketQuote(List<MarketPrices> marketPricesList) {
        return Optional.of(marketPricesList).orElse(Lists.newArrayList()).stream().filter(Objects::nonNull)
                .map(quote -> {
                    //计算平均价格
                    List<Double> pricesList = new ArrayList<>(quote.getLastPrices());
                    pricesList.add(quote.getBuy());
                    pricesList.add(quote.getSell());
                    Double middle = pricesList.stream().collect(Collectors.averagingDouble(amt -> RateCalculator.multiply(BigDecimal.valueOf(amt), BigDecimal.ONE).doubleValue()));
                    return new SpotRate(quote.getCcyPair().getCcy1().getCcyCode(), quote.getCcyPair().getCcy2().getCcyCode(), middle, new Date());
                }).collect(Collectors.toList());
    }

}
