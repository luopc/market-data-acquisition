package com.luopc.platform.web.mds.rates.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.luopc.platform.market.api.CcyPair;
import com.luopc.platform.market.api.Quotation;
import com.luopc.platform.market.api.Tenor;
import com.luopc.platform.market.tools.QuotationHelper;
import com.luopc.platform.market.tools.RateCalculator;
import com.luopc.platform.common.core.util.SmartRandomUtil;
import com.luopc.platform.web.mds.handler.event.BankQuotationEvent;
import com.luopc.platform.web.mds.rates.domain.dto.BankQuotation;
import com.luopc.platform.web.mds.rates.domain.dto.BankQuoteByTenor;
import com.luopc.platform.web.mds.rates.domain.dto.QuoteByTenor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Robin
 */
@Slf4j
@Setter
@Service
public class BankQuotationRepository {

    private final Map<String, Map<CcyPair, BankQuotation>> bankQuotationMapByBank = new ConcurrentHashMap<>();
    private final Map<CcyPair, Map<String, BankQuotation>> bankQuotationMapByCcyPair = new ConcurrentHashMap<>();
    private final Map<String, BankQuoteByTenor> bankQuoteByTenorMap = new ConcurrentHashMap<>();

    public List<BankQuotation> getBankQuotationFromCache(CcyPair ccyPair) {
        List<BankQuotation> bankQuotationList = Lists.newArrayList();
        Map<String, BankQuotation> bankQuotationMap = bankQuotationMapByCcyPair.get(ccyPair);
        if (Objects.nonNull(bankQuotationMap)) {
            bankQuotationList.addAll(bankQuotationMap.values());
        }
        return bankQuotationList;
    }

    public BankQuotation getBankQuotation(String bankCode, CcyPair ccyPair) {
        Map<CcyPair, BankQuotation> quotationMapForBank = bankQuotationMapByBank.get(bankCode);
        if (Objects.nonNull(quotationMapForBank)) {
            return quotationMapForBank.get(ccyPair);
        }
        return null;
    }

    public BankQuotation getBankQuotation(String bankCode, String ccyPair) {
        return getBankQuotation(bankCode, CcyPair.getInstance(ccyPair));
    }

    public QuoteByTenor getQuoteByCcyPairAndValueDate(String bankQuote, CcyPair ccyPair, LocalDate valueDate) {
        BankQuoteByTenor bankQuoteByTenor = bankQuoteByTenorMap.get(bankQuote);
        if (Objects.nonNull(bankQuoteByTenor)) {
            return bankQuoteByTenor.get(ccyPair, valueDate);
        } else {
            return null;
        }
    }


    public List<QuoteByTenor> getQuoteListByCcyPairAndTenor(CcyPair ccyPair, Tenor tenor) {
        List<QuoteByTenor> quoteByTenorList = Lists.newArrayList();
        for (BankQuoteByTenor bankQuoteByTenor : bankQuoteByTenorMap.values()) {
            quoteByTenorList.add(bankQuoteByTenor.get(ccyPair, tenor));
        }
        return quoteByTenorList;
    }

    public List<QuoteByTenor> getQuoteListByCcyPairAndValueDate(CcyPair ccyPair, LocalDate valueDate) {
        List<QuoteByTenor> quoteByTenorList = Lists.newArrayList();
        for (BankQuoteByTenor bankQuoteByTenor : bankQuoteByTenorMap.values()) {
            quoteByTenorList.add(bankQuoteByTenor.get(ccyPair, valueDate));
        }
        return quoteByTenorList;
    }

    private void updateBankQuotation(BankQuotation bankQuotation) {
        String bankCode = bankQuotation.getBankCode();
        String quoteCcy = bankQuotation.getQuoteCcy();
        String baseCcy = bankQuotation.getBaseCcy();
        CcyPair ccyPair = CcyPair.getInstance(baseCcy, quoteCcy);

        updateBankQuotationByCurrency(bankCode, ccyPair, bankQuotation);
        updateBankQuotationByBank(bankCode, ccyPair, bankQuotation);
        updateBankQuotationWithTenor(bankCode, bankQuotation);
    }

    public void updateBankQuotationWithTenor(String bankCode, Quotation quotation) {
        String baseCcy = quotation.getBaseCcy();
        String quoteCcy = quotation.getQuoteCcy();
        BigDecimal baseCcyInterestRate = RateCalculator.randomDiscount(0.01);
        BigDecimal quoteCcyInterestRate = RateCalculator.randomDiscount(0.01);
        for (Tenor tenor : Tenor.values()) {
            BigDecimal exchangeSell = RateCalculator.calculateForwardRateByTenor(QuotationHelper.getExchangeSell(quotation), baseCcyInterestRate, quoteCcyInterestRate, tenor.getDays());
            BigDecimal exchangeBuy = RateCalculator.calculateForwardRateByTenor(QuotationHelper.getExchangeBuy(quotation), baseCcyInterestRate, quoteCcyInterestRate, tenor.getDays());
            QuoteByTenor quoteByTenor = new QuoteByTenor(bankCode, tenor, CcyPair.getInstance(baseCcy, quoteCcy), exchangeBuy, exchangeSell, RateCalculator.div(exchangeBuy.add(exchangeSell), BigDecimal.valueOf(2)));
            updateQuotationByTenor(bankCode, tenor, CcyPair.getInstance(baseCcy, quoteCcy), quoteByTenor);
        }
    }

    private void updateQuotationByTenor(String bankCode, Tenor tenor, CcyPair ccyPair, QuoteByTenor quoteByTenor) {
        BankQuoteByTenor bankQuoteByTenor = bankQuoteByTenorMap.computeIfAbsent(bankCode, k -> new BankQuoteByTenor(bankCode));
        bankQuoteByTenor.update(tenor, ccyPair, quoteByTenor);
    }

    private void updateBankQuotationByCurrency(String bankCode, CcyPair ccyPair, BankQuotation bankQuotation) {
        Map<String, BankQuotation> quotationMapForBank = bankQuotationMapByCcyPair.computeIfAbsent(ccyPair, k -> Maps.newConcurrentMap());
        quotationMapForBank.computeIfAbsent(bankCode, k -> bankQuotation);

    }

    private void updateBankQuotationByBank(String bankCode, CcyPair ccyPair, BankQuotation bankQuotation) {
        Map<CcyPair, BankQuotation> quotationMapForBank = bankQuotationMapByBank.computeIfAbsent(bankCode, k -> Maps.newConcurrentMap());
        quotationMapForBank.computeIfAbsent(ccyPair, k -> bankQuotation);
    }


    public void updateBankQuotationList(List<BankQuotation> bankQuotationList) {
        Optional.ofNullable(bankQuotationList).orElse(Lists.newArrayList()).forEach(this::updateBankQuotation);
    }

    @Async
    @EventListener(value = BankQuotationEvent.class)
    public void handleBankQuotationEvent(BankQuotationEvent bankQuotationEvent) {
        log.info("Receive BankQuotationEvent message, {}", bankQuotationEvent.getChangeList().size());
        this.updateBankQuotationList(bankQuotationEvent.getChangeList());
    }


}
