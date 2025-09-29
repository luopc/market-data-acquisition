package com.luopc.platform.web.mds.rates.service;

import com.alibaba.fastjson2.JSON;
import com.luopc.platform.web.mds.config.EconomicsApiConfig;
import com.luopc.platform.web.mds.jobs.bank.api.NowApiBankQuoteService;
import com.luopc.platform.web.mds.jobs.bank.dto.QuoteMessage;
import com.luopc.platform.web.mds.jobs.rates.api.FinanceRatesApiCallService;
import com.luopc.platform.web.mds.jobs.rates.service.MarketPricesRetrievingService;
import com.luopc.platform.web.mds.jobs.rates.service.MarketRatesRetrievingService;
import com.luopc.platform.web.mds.jobs.mapping.dto.CurrencyMapping;
import com.luopc.platform.web.mds.jobs.rates.dto.rates.FinanceRateMsg;
import com.luopc.platform.web.mds.jobs.rates.dto.market.MarketQuotationMsg;
import com.luopc.platform.web.mds.jobs.rates.dto.market.MarketRatesMsg;
import com.luopc.platform.web.mds.rates.manager.CcyMappingRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

@Slf4j
@Disabled
public class FinanceRatesApiCallServiceTest {
    @Mock
    EconomicsApiConfig economicsApiConfig;
    @Mock
    CcyMappingRepository marketDataRepository;
    @InjectMocks
    FinanceRatesApiCallService financeRatesApiCallService;
    @InjectMocks
    NowApiBankQuoteService nowApiBankQuoteService;
    @InjectMocks
    MarketRatesRetrievingService marketRatesRetrievingService;
    @InjectMocks
    MarketPricesRetrievingService marketPricesRetrievingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(economicsApiConfig.getNowapiUrl()).thenReturn("http://api.k780.com/");
        when(economicsApiConfig.getNowapiAppKey()).thenReturn("69070");
        when(economicsApiConfig.getNowapiSign()).thenReturn("2e3bee938fd4a7104181938a9d5d3d0f");
        when(economicsApiConfig.getNowapiFinanceRateCnyquot()).thenReturn("finance.rate_cnyquot");
        when(economicsApiConfig.getNowapiFinanceRate()).thenReturn("finance.rate");

        when(economicsApiConfig.getMarketQuotationUrl()).thenReturn("https://www.xtrendspeed.com/api/quotation/symbol/list/detail");
        when(economicsApiConfig.getMarketRatesUrl()).thenReturn("https://www.xe.com/api/protected/midmarket-converter/");
        when(economicsApiConfig.getMarketRatesAuthorization()).thenReturn("bG9kZXN0YXI6cHVnc25heA==");
        when(marketDataRepository.getCurrencyMappingByCcyCode("USD")).thenReturn(new CurrencyMapping("USD", "110", "美元", "美国", "Mock"));
    }

    @Test
    void testRetrieveQuote() {
        QuoteMessage result = nowApiBankQuoteService.retrieveBankQuote("USD");
        //log.info("Get quote from remote: {}", result);
        Assertions.assertEquals("CNY", result.getBaseCcy());
        Assertions.assertEquals("USD", result.getQuoteCcy());
        Assertions.assertNotNull(result.getResult());
    }

    @Test
    void testRetrieveCcyPairRate() {
        FinanceRateMsg result = financeRatesApiCallService.retrieveFinanceCNYRate("USD");
        //log.info("Get rate from remote: {}", result);
        Assertions.assertEquals("CNY", result.getResult().getScur());
        Assertions.assertEquals("USD", result.getResult().getTcur());
        Assertions.assertNotNull(result.getResult());
    }

    @Test
    void testRetrieveMarketQuotation() {
        MarketQuotationMsg marketQuotationMsg = marketPricesRetrievingService.retrieveMarketQuotation();
        Assertions.assertTrue(marketQuotationMsg.isSuccess());
        log.info("{}", marketQuotationMsg.isSuccess());
        //log.info("{}", marketQuotationMsg.getData());
        log.info("{}", marketQuotationMsg.getErrorInfo());
        Assertions.assertTrue(marketQuotationMsg.isSuccess());
    }

    @Test
    void testMarketRates() {
        String marketQuotationResult = marketRatesRetrievingService.retrieveMarketRates();
        MarketRatesMsg marketRatesMsg = JSON.parseObject(marketQuotationResult, MarketRatesMsg.class);
        Assertions.assertTrue(marketRatesMsg.isSuccess());
        log.info("{}", marketRatesMsg.isSuccess());
        //log.info("{}", marketRatesMsg.getRates());
        Assertions.assertTrue(marketRatesMsg.isSuccess());
    }


}
