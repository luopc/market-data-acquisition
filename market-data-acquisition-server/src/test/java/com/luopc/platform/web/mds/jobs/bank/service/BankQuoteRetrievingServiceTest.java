package com.luopc.platform.web.mds.jobs.bank.service;

import com.luopc.platform.common.core.env.SystemEnvironment;
import com.luopc.platform.common.core.env.SystemEnvironmentEnum;
import com.luopc.platform.market.api.Currency;
import com.luopc.platform.market.tools.RateFormatter;
import com.luopc.platform.web.config.SystemEnvironmentConfig;
import com.luopc.platform.web.mds.config.EconomicsApiConfig;
import com.luopc.platform.web.mds.jobs.bank.api.NowApiBankQuoteService;
import com.luopc.platform.web.mds.jobs.mapping.dto.CurrencyMapping;
import com.luopc.platform.web.mds.rates.domain.dto.BankQuotation;
import com.luopc.platform.web.mds.rates.manager.CcyMappingRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@Slf4j
class BankQuoteRetrievingServiceTest {
    @Mock
    EconomicsApiConfig economicsApiConfig;
    @Mock
    SystemEnvironmentConfig systemEnvironmentConfig;
    @Mock
    CcyMappingRepository ccyMappingRepository;
    @InjectMocks
    NowApiBankQuoteService nowApiBankQuoteService;
    @InjectMocks
    BankQuoteRetrievingService bankQuoteRetrievingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(ccyMappingRepository.getCurrencyMappingByCcyCode(anyString())).thenReturn(new CurrencyMapping("ccy", "ccyNum", "currencyName", "countryName", "sourcePlatform"));
        when(systemEnvironmentConfig.initEnvironment()).thenReturn(new SystemEnvironment("Local-Test", SystemEnvironmentEnum.SUP));

        when(economicsApiConfig.getBankQuoteUrl()).thenReturn("http://www.cnhuilv.com/bank/");
        when(economicsApiConfig.getNowapiUrl()).thenReturn("http://api.k780.com/");
        when(economicsApiConfig.getNowapiAppKey()).thenReturn("69070");
        when(economicsApiConfig.getNowapiSign()).thenReturn("2e3bee938fd4a7104181938a9d5d3d0f");
        when(economicsApiConfig.getNowapiFinanceRateCnyquot()).thenReturn("finance.rate_cnyquot");
        when(economicsApiConfig.getNowapiFinanceRate()).thenReturn("finance.rate");
        bankQuoteRetrievingService.setNowApiBankQuoteService(nowApiBankQuoteService);
    }

    @Test
    void testGetQuoteFromApi() {
        List<BankQuotation> quotationFromAPI = bankQuoteRetrievingService.getQuoteFromApi(Currency.getInstance("AUD"));
        quotationFromAPI.forEach(bankQuotation -> {
            String msg = RateFormatter.formatBankQuote(bankQuotation.getBankCode(), bankQuotation);
            log.info("{}", msg);
        });
    }


}
