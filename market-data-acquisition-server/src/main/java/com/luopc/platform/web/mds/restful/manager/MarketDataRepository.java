package com.luopc.platform.web.mds.restful.manager;

import com.google.common.collect.Lists;
import com.luopc.platform.market.api.*;
import com.luopc.platform.market.api.Currency;
import com.luopc.platform.market.tools.RateCalculator;
import com.luopc.platform.web.mds.convertors.MarketDataConvertor;
import com.luopc.platform.web.mds.jobs.bank.service.BankQuoteRetrievingService;
import com.luopc.platform.web.mds.jobs.rates.api.FinanceRatesApiCallService;
import com.luopc.platform.web.mds.rates.domain.dto.BankQuotation;
import com.luopc.platform.web.mds.rates.domain.dto.QuoteByTenor;
import com.luopc.platform.web.mds.rates.domain.entity.CcyInterestDO;
import com.luopc.platform.web.mds.rates.handler.impl.BankQuotationFeedHandler;

import com.luopc.platform.web.mds.rates.manager.*;
import jakarta.annotation.Resource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * @author Robin
 */
@Slf4j
@Setter
@Service
public class MarketDataRepository {


    @Resource
    private SpotRateRepository spotRateRepository;
    @Resource
    private ForwardRateRepository forwardRateRepository;
    @Resource
    private BankQuotationRepository bankQuotationRepository;
    @Resource
    private InterestRatesRepository interestRatesRepository;
    @Resource
    private MarketPricesRepository marketPricesRepository;
    @Resource
    private BankQuoteRetrievingService bankQuoteRetrievingService;
    @Resource
    private FinanceRatesApiCallService financeRatesApiCallService;
    @Resource
    private BankQuotationFeedHandler bankQuotationFeedHandler;

    public Map<CcyPair, SpotRate> getAllSpotRates() {
        return spotRateRepository.getAllSpotRates();
    }

    public Map<CcyPair, ForwardRate> getAllForwardRates() {
        return forwardRateRepository.getAllForwardRates();
    }


    public ForwardRate getForwardRates(CcyPair ccyPair) {
        return forwardRateRepository.getForwardRate(ccyPair);
    }


    public MarketPrices getMarketRate(CcyPair ccyPair) {
        return marketPricesRepository.getMarketPrices(ccyPair);
    }


    public Map<CcyPair, MarketPrices> getAllMarketRates() {
        return marketPricesRepository.getAllMarketQuotes();
    }

    public List<QuoteByTenor> getQuoteListByCcyPairAndValueDate(CcyPair ccyPair, LocalDate valueDate) {
        return bankQuotationRepository.getQuoteListByCcyPairAndValueDate(ccyPair, valueDate);
    }

    public List<QuoteByTenor> getQuoteListByCcyPairAndTenor(CcyPair ccyPair, Tenor tenor) {
        return bankQuotationRepository.getQuoteListByCcyPairAndTenor(ccyPair, tenor);
    }

    public SpotRate getSpotRate(CcyPair ccyPair) {
        return getSpotRate(ccyPair.getCcy1(), ccyPair.getCcy2());
    }

    public SpotRate getSpotRate(Currency priCcy, Currency cntCcy) {
        if (priCcy.equals(cntCcy)) {
            return new SpotRate(priCcy.getCcyCode(), cntCcy.getCcyCode(), 1D);
        } else {
            SpotRate ccyPairSpotRate = spotRateRepository.getSpotRate(CcyPair.getInstance(priCcy, cntCcy));
            if (Objects.nonNull(ccyPairSpotRate)) {
                return ccyPairSpotRate;
            } else {
                ccyPairSpotRate = spotRateRepository.getSpotRate(CcyPair.getInstance(cntCcy, priCcy));
                if (Objects.nonNull(ccyPairSpotRate)) {
                    double finalRate = RateCalculator.div(BigDecimal.ONE, ccyPairSpotRate.getRate()).doubleValue();
                    return new SpotRate(priCcy.getCcyCode(), cntCcy.getCcyCode(), finalRate);
                } else if (!priCcy.isUsdFlag() && !cntCcy.isUsdFlag()) {
                    SpotRate priSpotRate = spotRateRepository.getSpotRate(priCcy);
                    SpotRate cntSpotRate = spotRateRepository.getSpotRate(cntCcy);
                    if (Objects.nonNull(priSpotRate) && Objects.nonNull(cntSpotRate)) {
                        double finalRate = RateCalculator.calculateRateInDouble(priCcy.getCcyCode(), priSpotRate, cntCcy.getCcyCode(), cntSpotRate);
                        return new SpotRate(priCcy.getCcyCode(), cntCcy.getCcyCode(), finalRate);
                    }
                } else {
                    Currency queryCcy = priCcy.isUsdFlag() ? cntCcy : priCcy;
                    SpotRate spotRate = spotRateRepository.getSpotRate(queryCcy);
                    if (Objects.nonNull(spotRate)) {
                        double finalRate = RateCalculator.calculateRateInDouble(priCcy.getCcyCode(), spotRate);
                        return new SpotRate(priCcy.getCcyCode(), cntCcy.getCcyCode(), finalRate);
                    }
                }
            }
        }
        SpotRate ccyPairRateFromApi = financeRatesApiCallService.getSpotRateFromApi(priCcy, cntCcy);
        if (Objects.nonNull(ccyPairRateFromApi)) {
            spotRateRepository.updateSpotRate(ccyPairRateFromApi);
        }
        return ccyPairRateFromApi;
    }


    public List<BankQuotation> getBankQuote(CcyPair ccyPair) {
        List<BankQuotation> bankQuotations = bankQuotationRepository.getBankQuotationFromCache(ccyPair);
        if (CollectionUtils.isEmpty(bankQuotations)) {
            //Get from API
            List<BankQuotation> getQuoteFromApi = getQuoteFromApi(ccyPair);

            bankQuotationFeedHandler.onResponse(getQuoteFromApi);
            return getQuoteFromApi;
        }
        return bankQuotations;
    }


    public List<BankQuotation> getQuoteFromApi(CcyPair ccyPair) {
        Currency ccy1 = ccyPair.getCcy1();
        Currency ccy2 = ccyPair.getCcy2();
        if (ccy1.isCnyFlag() || ccy2.isCnyFlag()) {
            Currency quoteCcy = ccy1.isCnyFlag() ? ccy2 : ccy1;
            List<BankQuotation> bankQuotationList = bankQuoteRetrievingService.getQuoteFromApi(quoteCcy);
            bankQuotationFeedHandler.onResponse(bankQuotationList);
            log.info("BankQuotations from API: {}", bankQuotationList);
            return bankQuotationList;
        } else {
            //List<BankQuotation> bankQuotationForCcy1 = bankQuoteRetrievingService.getQuoteFromApi(ccy1);
            //List<BankQuotation> bankQuotationForCcy2 = bankQuoteRetrievingService.getQuoteFromApi(ccy2);
            SpotRate spotRate = getSpotRate(ccy1, ccy2);
            if (Objects.nonNull(spotRate)) {
                List<BankQuotation> bankQuotationList = MarketDataConvertor.convertSpotRateToBankQuote(spotRate);
                bankQuotationFeedHandler.onResponse(bankQuotationList);
                log.info("BankQuotations from Mock: {}", bankQuotationList);
                return bankQuotationList;
            }

        }
        return Lists.newArrayList();
    }


    public List<CcyInterestDO> getAllInterestRates() {
        return new ArrayList<>(interestRatesRepository.getAllInterestRates().values());
    }

    public CcyInterestDO getInterestRate(Currency currency) {
        return interestRatesRepository.getInterestRate(currency.getCcyCode());
    }


    private boolean isExpiry(Date update) {
        if (Objects.isNull(update)) {
            return true;
        }

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
        calendar.setTime(new Date());
        long now = calendar.getTime().getTime();
        log.info("update time is: {}", update);
        calendar.setTime(update);
        long updateTime = calendar.getTime().getTime();

        if ((now - updateTime) >= (30 * 24 * 3600 * 1000L)) {
            log.info("Update time in Millis: {}", (now - updateTime));
            log.info("Expiry time in Millis: {}", (7 * 24 * 60 * 60 * 1000));
            return true;
        }
        return false;
    }


    public Set<CcyPair> getMainCcyPairs() {
        Set<CcyPair> spotRateCcyPairs = spotRateRepository.getCcyPairs();
        Set<CcyPair> forwardRateCcyPairs = forwardRateRepository.getCcyPairs();
        spotRateCcyPairs.retainAll(forwardRateCcyPairs);
        Set<CcyPair> marketPricesCcyPairs = marketPricesRepository.getCcyPairs();
        spotRateCcyPairs.retainAll(marketPricesCcyPairs);
        return spotRateCcyPairs;
    }

}
