package com.luopc.platform.web.mds.jobs;

import com.alibaba.fastjson2.JSON;
import com.luopc.platform.market.api.SpotRate;
import com.luopc.platform.web.mds.config.EconomicsApiConfig;
import com.luopc.platform.web.mds.jobs.rates.dto.market.MarketRatesMsg;
import com.luopc.platform.web.mds.jobs.rates.dto.rates.ExchangeRatesMsg;
import com.luopc.platform.web.mds.jobs.rates.service.CurrencyRateRetrievingService;
import com.luopc.platform.web.mds.jobs.rates.service.MarketRatesRetrievingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.when;

@Slf4j
class MarketRatesRetrievingServiceTest {
    @Mock
    EconomicsApiConfig economicsApiConfig;
    @InjectMocks
    MarketRatesRetrievingService marketRatesRetrievingService;
    @InjectMocks
    CurrencyRateRetrievingService currencyRateRetrievingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(economicsApiConfig.getMarketRatesUrl()).thenReturn("https://www.xe.com/api/protected/midmarket-converter/");
        when(economicsApiConfig.getMarketRatesAuthorization()).thenReturn("bG9kZXN0YXI6cHVnc25heA==");
        when(economicsApiConfig.getExchangeRatesUrl()).thenReturn("https://www.chinamoney.com.cn/r/cms/www/chinamoney/data/fx/sdds-exch-rate.json");
    }

    @Test
    void testRetrieveMarketRates() {
        String marketRatesResult = marketRatesRetrievingService.retrieveMarketRates();
        MarketRatesMsg marketRatesMsg = JSON.parseObject(marketRatesResult, MarketRatesMsg.class);
        log.info("marketRatesMsg: {}",marketRatesMsg);
        Assertions.assertTrue(marketRatesMsg.isSuccess());
    }

    @Test
    void testRetrieveExchangeRates() {
        String exchangeRatesResult = currencyRateRetrievingService.retrieveExchangeRates();
        ExchangeRatesMsg exchangeRatesMsg = JSON.parseObject(exchangeRatesResult, ExchangeRatesMsg.class);
        List<SpotRate> chinaSpotRateList = marketRatesRetrievingService.extractedSpotRates(exchangeRatesMsg);
        Assertions.assertTrue(CollectionUtils.isNotEmpty(exchangeRatesMsg.getRecords()));
        Assertions.assertTrue(CollectionUtils.isNotEmpty(chinaSpotRateList));
    }

}
