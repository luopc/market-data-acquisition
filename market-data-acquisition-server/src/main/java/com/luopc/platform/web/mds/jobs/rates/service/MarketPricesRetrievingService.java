package com.luopc.platform.web.mds.jobs.rates.service;

import com.alibaba.fastjson2.JSON;
import com.luopc.platform.market.api.*;
import com.luopc.platform.web.mds.jobs.common.NowApiCallService;
import com.luopc.platform.web.mds.jobs.rates.dto.market.MarketQuotationMsg;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author by Robin
 * @className MarketQuotesRetrievingService
 * @description 市场货币交易数据
 * @date 2024/1/6 0006 10:55
 */
@Slf4j
@Setter
@Service
@NoArgsConstructor
public class MarketPricesRetrievingService extends NowApiCallService {

    public List<MarketPrices> getMarketPricesList() {
        MarketQuotationMsg marketQuotationMsg = retrieveMarketQuotation();
        if (!marketQuotationMsg.isSuccess()) {
            log.error("unable to RetrievingMarketQuotation from API, please take a look.");
            marketQuotationMsg = getLocalData();
        }
        List<MarketPrices> marketPricesList = marketQuotationMsg.getData().getFirst().getCodeList().stream()
                .filter(marketQuoteDTO -> marketQuoteDTO.getCode().length() == 6)
                .map(quote -> {
                    CcyPair ccyPair = CcyPair.getInstance(quote.getCode());
                    return new MarketPrices(ccyPair,
                            Double.valueOf(quote.getBuy()),
                            Double.valueOf(quote.getSell()),
                            quote.getLastPrices().stream().map(Double::valueOf).collect(Collectors.toList()));
                }).collect(Collectors.toList());
        return marketPricesList;
    }

    private MarketQuotationMsg getLocalData() {
        String sourceFile = "static/marketQuotation.json";
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(sourceFile)) {
            return JSON.parseObject(inputStream, MarketQuotationMsg.class);
        } catch (IOException var4) {
            log.error("Unable to retrieve data from Local, sourceFile = {}", sourceFile, var4);
            return null;
        }
    }

    public MarketQuotationMsg retrieveMarketQuotation() {
        String url = economicsApiConfig.getMarketQuotationUrl();
        log.info("MarketQuotation Request url: {}", url);
        Map<String, Object> params = new HashMap<>(3);
        params.put("pageSize", 100);
        params.put("page", 1);
        params.put("webCodeType", "1");
        String result = postData(url, params);
        return JSON.parseObject(result, MarketQuotationMsg.class);
    }
}
