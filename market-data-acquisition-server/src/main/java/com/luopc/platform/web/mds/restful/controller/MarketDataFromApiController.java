package com.luopc.platform.web.mds.restful.controller;

import com.luopc.platform.market.api.MarketPrices;
import com.luopc.platform.market.api.SpotRate;
import com.luopc.platform.web.mds.convertors.MarketDataConvertor;
import com.luopc.platform.web.mds.jobs.rates.service.MarketPricesRetrievingService;
import com.luopc.platform.web.mds.jobs.rates.service.MarketRatesRetrievingService;
import com.luopc.platform.web.result.ResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/market", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "MarketDataFromApiController", description = "从 https://www.xtrendspeed.com 中获取到最新的汇率信息")
public class MarketDataFromApiController {

    @Resource
    private MarketPricesRetrievingService marketPricesRetrievingService;
    @Resource
    private MarketRatesRetrievingService marketRatesRetrievingService;

    @GetMapping("/getMarketPrices")
    @Operation(summary = "获取trading economics的利率数据", description = "不需要登录后访问")
    public ResponseMessage<Object> getAllMarketPrices() {
        List<MarketPrices> marketPricesList = marketPricesRetrievingService.getMarketPricesList();
        return ResponseMessage.success(marketPricesList.stream().map(MarketDataConvertor::getMarketQuoteVO).toList());
    }

    @GetMapping("/getMarketRates")
    @Operation(summary = "获取Active market spot rates", description = "不需要登录后访问")
    public ResponseMessage<Object> getMarketSpotRates() {
        List<SpotRate> marketSpotRateList = marketRatesRetrievingService.getAggregatedSpotRates();
        return ResponseMessage.success(marketSpotRateList.stream().map(MarketDataConvertor::spotRateToVo).toList());
    }
}
