package com.luopc.platform.web.mds.jobs;

import com.luopc.platform.web.mds.config.EconomicsApiConfig;
import com.luopc.platform.web.mds.jobs.mapping.dto.CurrencyMapping;
import com.luopc.platform.web.mds.jobs.mapping.service.CcyMappingRetrievingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

class CurrencyMappingServiceTest {
    @Mock
    EconomicsApiConfig economicsApiConfig;
    @InjectMocks
    CcyMappingRetrievingService ccyMappingRetrievingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(economicsApiConfig.getIbanMappingUrl()).thenReturn("https://www.iban.hk/currency-codes");
        when(economicsApiConfig.getCurrencyMappingUrl()).thenReturn("http://www.cnhuilv.com/currency/");
    }

    @Test
    void testGetCurrencyMappingFromIban() {
        List<CurrencyMapping> result = ccyMappingRetrievingService.getCurrencyMappingFromIban();
//        Assertions.assertEquals(268, result.size());
        Assertions.assertFalse(result.isEmpty());
        Map<String, CurrencyMapping> currencyMappingMap = result.stream().
                collect(Collectors.toMap(CurrencyMapping::getCcy, m -> m, (o, n) -> n));
        CurrencyMapping currencyMapping = currencyMappingMap.get("JPY");
        Assertions.assertEquals("JPY", currencyMapping.getCcy());
        Assertions.assertEquals("日元", currencyMapping.getCurrencyName());
        Assertions.assertEquals("日本", currencyMapping.getCountryName());
        Assertions.assertEquals("392", currencyMapping.getCcyNum());
    }

    @Test
    void testGetCurrencyMappingFromLocal() {
        List<CurrencyMapping> result = ccyMappingRetrievingService.getCurrencyMappingFromLocal();
        Assertions.assertEquals(171, result.size());
        Map<String, CurrencyMapping> currencyMappingMap = result.stream().
                collect(Collectors.toMap(CurrencyMapping::getCcy, m -> m, (o, n) -> n));
        CurrencyMapping currencyMapping = currencyMappingMap.get("JPY");
        Assertions.assertEquals("JPY", currencyMapping.getCcy());
        Assertions.assertEquals("日元 - Japanese Yen", currencyMapping.getCurrencyName());
        Assertions.assertEquals("日本", currencyMapping.getCountryName());
        Assertions.assertEquals("392", currencyMapping.getCcyNum());
    }

    //    @Test
    void testGetCurrencyMappingFromCnHuiLv() {
        List<CurrencyMapping> result = ccyMappingRetrievingService.getCurrencyMappingFromCnHuiLv();
        Assertions.assertFalse(result.isEmpty());
    }
}
