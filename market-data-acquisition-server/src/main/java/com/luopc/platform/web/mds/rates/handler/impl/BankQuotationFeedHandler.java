package com.luopc.platform.web.mds.rates.handler.impl;

import com.luopc.platform.web.mds.handler.publisher.MarketDataUpdateEventPublisher;
import com.luopc.platform.web.mds.rates.domain.dto.BankQuotation;
import com.luopc.platform.web.mds.rates.handler.AbstractMarketDataFeedHandler;
import com.luopc.platform.web.mds.rates.handler.MarketDataFeedHandler;
import com.luopc.platform.web.mds.rates.manager.BankQuotationRepository;
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
public class BankQuotationFeedHandler extends AbstractMarketDataFeedHandler<BankQuotation> implements MarketDataFeedHandler<BankQuotation> {

    @Resource
    private BankQuotationRepository bankQuotationRepository;
    @Resource
    private MarketDataUpdateEventPublisher marketDataUpdateEventPublisher;

    @Override
    protected void processMessage(Collection<BankQuotation> entityList) {
        if (CollectionUtils.isNotEmpty(entityList)) {
            marketDataUpdateEventPublisher.publishBankQuote(new ArrayList<>(entityList));
        }
    }

    @Override
    protected void processInitialLoadMsg(Collection<BankQuotation> initialList) {
        bankQuotationRepository.updateBankQuotationList(new ArrayList<>(initialList));
    }

}
