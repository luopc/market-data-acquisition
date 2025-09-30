package com.luopc.platform.web.mds.jobs;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.luopc.platform.market.api.CountryMapCurrency;
import com.luopc.platform.web.mds.jobs.mapping.dto.CurrencyMapping;
import com.luopc.platform.web.mds.jobs.mapping.service.CcyMappingRetrievingService;
import com.luopc.platform.web.mds.rates.domain.entity.CurrencyMappingDO;
import com.luopc.platform.web.mds.rates.handler.impl.CurrencyMappingFeedHandler;
import com.luopc.platform.web.mds.rates.mappers.CurrencyMappingMapper;
import jakarta.annotation.Resource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Robin
 * @description 更新货币和国家对应代码
 */
@Slf4j
@Setter
@Component
public class CcyMappingRetrievingTask {

    @Resource
    private CurrencyMappingMapper currencyMappingMapper;
    @Resource
    private CurrencyMappingFeedHandler currencyMappingFeedHandler;
    @Resource
    private CcyMappingRetrievingService ccyMappingRetrievingService;

    public void initialLoad() {
        List<CurrencyMappingDO> currencyMappingDOList = currencyMappingMapper.initialLoad();
        if (CollectionUtil.isNotEmpty(currencyMappingDOList)) {
            log.info("Going to process CurrencyMapping from InitialLoad, size = {}", currencyMappingDOList.size());
            List<CurrencyMapping> currencyMappingList = currencyMappingDOList.stream()
                    .map(this::currencyMappingDoToBO)
                    .filter(Objects::nonNull).collect(Collectors.toList());
            currencyMappingFeedHandler.onInitialLoad(currencyMappingList);
        } else {
            currencyMappingFeedHandler.onInitialLoad(Lists.newArrayList());
            Map<String, String> countryAdnCcy = CountryMapCurrency.getCountryAndCcyMapping();

            List<CurrencyMapping> currencyMappingList = Lists.newArrayList();
            currencyMappingList.addAll(ccyMappingRetrievingService.getCurrencyMappingFromIban());
            currencyMappingList.addAll(ccyMappingRetrievingService.getCurrencyMappingFromLocal());
            Map<String, CurrencyMapping> currencyMappingMap = currencyMappingList.stream().collect(Collectors.toMap(CurrencyMapping::getCcy, k -> k, (o, n) -> n));

            List<CurrencyMapping> resultList = Lists.newArrayList();
            countryAdnCcy.forEach((countryName, ccy) -> {
                CurrencyMapping currencyMapping = currencyMappingMap.get(ccy);
                if (Objects.nonNull(currencyMapping)) {
                    currencyMapping.setCountryName(countryName);
                    resultList.add(currencyMapping);
                }
            });

            log.info("Going to phase CurrencyMapping from API, size = {}", resultList.size());
            currencyMappingFeedHandler.onResponse(resultList);
        }
    }

    public CurrencyMapping currencyMappingDoToBO(CurrencyMappingDO currencyMappingDO) {
        if (currencyMappingDO == null) {
            return null;
        } else {
            CurrencyMapping currencyMapping = new CurrencyMapping();
            if (currencyMappingDO.getCcyNum() != null) {
                currencyMapping.setCcyNum(String.valueOf(currencyMappingDO.getCcyNum()));
            }
            currencyMapping.setCcy(currencyMappingDO.getCcy());
            currencyMapping.setCurrencyName(currencyMappingDO.getCurrencyName());
            currencyMapping.setCountryName(currencyMappingDO.getCountryName());
            currencyMapping.setSourcePlatform("DB");
            return currencyMapping;
        }
    }

}
