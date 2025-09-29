package com.luopc.platform.web.mds.jobs;

import com.luopc.platform.web.mds.config.EconomicsApiConfig;
import com.luopc.platform.web.mds.convertors.MapStructConvertor;
import com.luopc.platform.web.mds.jobs.mapping.dto.CurrencyMapping;
import com.luopc.platform.web.mds.jobs.mapping.service.CcyMappingRetrievingService;
import com.luopc.platform.web.mds.jobs.rates.service.InterestRatesRetrievingService;
import com.luopc.platform.web.mds.rates.domain.entity.CcyInterestDO;
import com.luopc.platform.web.mds.rates.manager.CcyMappingRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class InterestRatesRetrievingServiceTest {

    @Mock
    EconomicsApiConfig economicsApiConfig;
    @Spy
    MapStructConvertor mapStructConvertor;
    @Spy
    CcyMappingRepository ccyMappingRepository;
    @InjectMocks
    InterestRatesRetrievingService interestRatesRetrievingService;

    @BeforeEach
    void setUp() {
        CcyMappingRetrievingService ccyMappingRetrieving = new CcyMappingRetrievingService();
        List<CurrencyMapping> currencyMappingList = ccyMappingRetrieving.getCurrencyMappingFromLocal();
        ccyMappingRepository.updateCurrencyMappingList(currencyMappingList);
        interestRatesRetrievingService.setMapStructConvertor(mapStructConvertor);
    }

    @Test
    void testGetInterestRates() {
        when(economicsApiConfig.getInterestRateUrl()).thenReturn("https://zh.tradingeconomics.com/country-list/interest-rate?continent=");
        when(economicsApiConfig.getInterestContinents()).thenReturn("africa,asia,australia,europe,america,g20,all");

        List<CcyInterestDO> result = interestRatesRetrievingService.getInterestRates();
        result.forEach(rate -> {
            log.info("countryMap.put(\"{}\",\"{}\"), rate = {}, PreRate = {};", rate.getCountryName(), rate.getCcy(), rate.getRate(), rate.getPreRate());
        });


    }


}
