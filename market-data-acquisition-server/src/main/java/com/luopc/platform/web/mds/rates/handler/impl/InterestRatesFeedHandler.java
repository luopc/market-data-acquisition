package com.luopc.platform.web.mds.rates.handler.impl;

import com.luopc.platform.web.mds.handler.publisher.MarketDataUpdateEventPublisher;
import com.luopc.platform.web.mds.rates.domain.entity.CcyInterestDO;
import com.luopc.platform.web.mds.rates.handler.AbstractMarketDataFeedHandler;
import com.luopc.platform.web.mds.rates.handler.MarketDataFeedHandler;
import com.luopc.platform.web.mds.rates.manager.InterestRatesRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Robin
 */
@Slf4j
@Component
public class InterestRatesFeedHandler extends AbstractMarketDataFeedHandler<CcyInterestDO> implements MarketDataFeedHandler<CcyInterestDO> {

    @Resource
    private InterestRatesRepository interestRatesRepository;
    @Resource
    private MarketDataUpdateEventPublisher marketDataUpdateEventPublisher;

    @Override
    protected void processInitialLoadMsg(Collection<CcyInterestDO> initialList) {
        log.info("Update interestRateInitialEvent message, {}", initialList.size());
        interestRatesRepository.updateInterestRateList(new ArrayList<>(initialList));
    }

    @Override
    protected void processMessage(Collection<CcyInterestDO> responseList) {
        if (CollectionUtils.isNotEmpty(responseList)) {
            marketDataUpdateEventPublisher.publishInterestRate(new ArrayList<>(responseList));
        }
    }
}
