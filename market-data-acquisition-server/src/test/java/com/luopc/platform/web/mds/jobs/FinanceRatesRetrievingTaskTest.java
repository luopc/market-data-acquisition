package com.luopc.platform.web.mds.jobs;

import com.luopc.platform.market.api.Currency;
import com.luopc.platform.market.api.SpotRate;
import com.luopc.platform.market.tools.RateCalculator;
import com.luopc.platform.web.mds.jobs.rates.api.FinanceRatesApiCallService;
import com.luopc.platform.web.mds.jobs.rates.dto.rates.FinanceRate;
import com.luopc.platform.web.mds.jobs.rates.dto.rates.FinanceRateMsg;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class FinanceRatesRetrievingTaskTest {

    @Mock
    FinanceRatesApiCallService financeRatesApiCallService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(financeRatesApiCallService.getSpotRateFromApi(any(), any())).thenCallRealMethod();
        when(financeRatesApiCallService.retrieveFinanceRate(any(), any())).thenReturn(new FinanceRateMsg(0, null));
        when(financeRatesApiCallService.retrieveFinanceRate("USD", "CNY")).thenReturn(new FinanceRateMsg(1, new FinanceRate("ALREADY", "USD", "CNY", "美元/人民币", 7.2135, new Date())));
        when(financeRatesApiCallService.retrieveFinanceRate("GBP", "CNY")).thenReturn(new FinanceRateMsg(1, new FinanceRate("ALREADY", "GBP", "CNY", "英镑/人民币", 9.02602589, new Date())));
        when(financeRatesApiCallService.retrieveFinanceRate("CNY", "EUR")).thenReturn(new FinanceRateMsg(1, new FinanceRate("ALREADY", "CNY", "EUR", "人民币/欧元", 0.12884831, new Date())));
        when(financeRatesApiCallService.retrieveFinanceUSDRate("GBP")).thenReturn(new FinanceRateMsg(1, new FinanceRate("ALREADY", "USD", "GBP", "美元/英镑", 0.78883, new Date())));
    }

    @Test
    void testRetrieveSpotRate() {
        SpotRate ccyPairRate = financeRatesApiCallService.getSpotRateFromApi(Currency.getInstance("USD"), Currency.getInstance("GBP"));
        Assertions.assertEquals("USD", ccyPairRate.getCcyPair().getCcy1().getCcyCode());
        Assertions.assertEquals("GBP", ccyPairRate.getCcyPair().getCcy2().getCcyCode());
        //real rate = 0.78883
        Assertions.assertEquals(0.78883, ccyPairRate.getRate().doubleValue());
        //real rate = 1.26770026
        Assertions.assertEquals(1.2677, RateCalculator.div(BigDecimal.ONE, ccyPairRate.getRate()).doubleValue());
    }

}
