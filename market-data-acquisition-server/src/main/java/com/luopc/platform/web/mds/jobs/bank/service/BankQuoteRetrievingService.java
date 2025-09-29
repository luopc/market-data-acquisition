package com.luopc.platform.web.mds.jobs.bank.service;

import com.google.common.collect.Lists;
import com.luopc.platform.market.api.Currency;
import com.luopc.platform.web.mds.convertors.MarketDataConvertor;
import com.luopc.platform.web.mds.jobs.bank.api.NowApiBankQuoteService;
import com.luopc.platform.web.mds.jobs.bank.dto.QuoteMessage;
import com.luopc.platform.web.mds.jobs.mapping.dto.CurrencyMapping;
import com.luopc.platform.web.mds.rates.domain.dto.BankQuotation;
import com.luopc.platform.web.mds.rates.manager.CcyMappingRepository;
import jakarta.annotation.Resource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @author by Robin
 * @className BankQuoteRetrieveService
 * @description TODO
 * @date 2024/1/6 0006 10:23
 */
@Slf4j
@Setter
@Component
public class BankQuoteRetrievingService {

    @Resource
    private CcyMappingRepository ccyMappingRepository;
    @Resource
    private NowApiBankQuoteService nowApiBankQuoteService;

    public List<BankQuotation> getQuoteFromApi(Currency baseCcy) {
        QuoteMessage quoteMessage = nowApiBankQuoteService.retrieveBankQuote(baseCcy.getCcyCode());
        if (quoteMessage.isSuccess()) {
            return getBankQuoteForCcy(baseCcy, quoteMessage);
        } else {
            log.warn("Market Quote cannot be found, quoteCcy={}", baseCcy);
        }
        return Lists.newArrayList();
    }

    private List<BankQuotation> getBankQuoteForCcy(Currency baseCcy, QuoteMessage quoteMessage) {
        List<BankQuotation> exchangeQuotationList = quoteMessage.getBankExchangeQuotationList();
        exchangeQuotationList.addAll(MarketDataConvertor.convertBankQuotation(exchangeQuotationList));
        exchangeQuotationList.forEach(exchangeQuotation -> {
            CurrencyMapping mapping = ccyMappingRepository.getCurrencyMappingByCcyCode(exchangeQuotation.getQuoteCcy());
            if (Objects.nonNull(mapping)) {
                exchangeQuotation.setQuoteCcyName(mapping.getCurrencyName());
            }
        });
        return exchangeQuotationList;
    }

}
