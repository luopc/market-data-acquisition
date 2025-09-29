package com.luopc.platform.web.mds.rates.service;

import com.luopc.platform.web.mds.config.EconomicsApiConfig;
import com.luopc.platform.web.mds.jobs.bank.api.IBankDataApiCallService;
import com.luopc.platform.web.mds.rates.domain.dto.BankQuotation;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.when;

@Slf4j
class IBankDataApiCallServiceTest {
    @Mock
    EconomicsApiConfig economicsApiConfig;
    @InjectMocks
    IBankDataApiCallService iBankDataApiCallService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(economicsApiConfig.getBankQuoteUrl()).thenReturn("http://www.cnhuilv.com/bank/");
    }

    @Test
    void testGetExchangeQuotationFromAPI() {
        List<BankQuotation> result = iBankDataApiCallService.getExchangeQuotationFromAPI();
        result.forEach(bankQuotation -> {
            //log.info("{}", bankQuotation);
        });
    }

}
