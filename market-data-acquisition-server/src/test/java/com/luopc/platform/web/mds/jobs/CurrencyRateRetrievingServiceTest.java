package com.luopc.platform.web.mds.jobs;

import com.luopc.platform.market.api.SpotRate;
import com.luopc.platform.web.mds.config.EconomicsApiConfig;
import com.luopc.platform.web.mds.jobs.rates.service.CurrencyRateRetrievingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;

class CurrencyRateRetrievingServiceTest {
    @Mock
    EconomicsApiConfig economicsApiConfig;
    @InjectMocks
    CurrencyRateRetrievingService currencyRateRetrievingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(economicsApiConfig.getCurrencyRateUrl()).thenReturn("https://zh.tradingeconomics.com/currencies");
    }

    @Test
    void testGetInterestRates() {
        List<SpotRate> result = currencyRateRetrievingService.getCurrencyRates();
        System.out.println(result);
    }

}
