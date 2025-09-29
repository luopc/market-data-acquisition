package com.luopc.platform.web.mds.convertors;

import com.google.common.collect.Lists;
import com.luopc.platform.market.api.*;
import com.luopc.platform.market.tools.QuotationHelper;
import com.luopc.platform.market.tools.RateCalculator;
import com.luopc.platform.web.mds.rates.domain.dto.BankQuotation;
import com.luopc.platform.web.mds.rates.domain.dto.QuoteByTenor;
import com.luopc.platform.web.mds.rates.domain.entity.SpotRateDO;
import com.luopc.platform.web.mds.restful.domain.vo.MarketQuoteVO;
import com.luopc.platform.web.mds.restful.domain.vo.QuoteVO;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author by Robin
 * @className MarketDataConvertor
 * @description TODO
 * @date 2024/1/6 0006 17:01
 */
public class MarketDataConvertor {

    public static QuoteVO convertToQuoteVO(QuoteByTenor quoteByTenor) {
        QuoteVO quoteVO = new QuoteVO();
        quoteVO.setBankCode(quoteByTenor.getBankCode());
        quoteVO.setBuy(quoteByTenor.getBuy().doubleValue());
        quoteVO.setSell(quoteByTenor.getSell().doubleValue());
        quoteVO.setMiddle(quoteByTenor.getMiddle().doubleValue());
        return quoteVO;
    }

    public static QuoteVO convertToQuoteVO(BankQuotation bankQuotation) {
        QuoteVO quoteVO = new QuoteVO();
        quoteVO.setBankCode(bankQuotation.getBankCode());
        quoteVO.setBankName(bankQuotation.getBankName());
        quoteVO.setQuoteCcy(bankQuotation.getQuoteCcy());
        quoteVO.setBaseCcy(bankQuotation.getBaseCcy());
        quoteVO.setBuy(QuotationHelper.getExchangeBuy(bankQuotation).doubleValue());
        quoteVO.setSell(QuotationHelper.getExchangeSell(bankQuotation).doubleValue());
        quoteVO.setMiddle(QuotationHelper.getMiddle(bankQuotation).doubleValue());
        quoteVO.setLastUpdateTime(bankQuotation.getUpdateTime());
        return quoteVO;
    }

    public static MarketQuoteVO getMarketQuoteVO(MarketPrices marketPrices) {
        MarketQuoteVO marketQuoteVO = new MarketQuoteVO();
        marketQuoteVO.setBaseCcy(marketPrices.getCcyPair().getCcy1().getCcyCode());
        marketQuoteVO.setQuoteCcy(marketPrices.getCcyPair().getCcy2().getCcyCode());
        marketQuoteVO.setBuy(marketPrices.getBuy());
        marketQuoteVO.setSell(marketPrices.getSell());
        List<Double> lastPrices = marketPrices.getLastPrices();
        marketQuoteVO.setLastPrices(lastPrices);

        //计算平均价格
        List<Double> pricesList = new ArrayList<>(lastPrices);
        pricesList.add(marketPrices.getBuy());
        pricesList.add(marketPrices.getSell());
        marketQuoteVO.setMiddle(pricesList.stream().collect(Collectors.averagingDouble(amt -> RateCalculator.multiply(BigDecimal.valueOf(amt), BigDecimal.ONE).doubleValue())));
        return marketQuoteVO;
    }


    public static SpotRateDO spotRateToDO(SpotRate spotRate) {
        SpotRateDO spotRateDO = new SpotRateDO();
        spotRateDO.setCcy1(spotRate.getCcyPair().getCcy1().getCcyCode());
        spotRateDO.setCcy2(spotRate.getCcyPair().getCcy2().getCcyCode());
        spotRateDO.setRate(spotRate.getRate().doubleValue());
        spotRateDO.setUseConversion(RateCalculator.div(BigDecimal.ONE, spotRate.getRate()).doubleValue());
        Currency nonUsdCcy = spotRate.getCcyPair().getNonUsdCcy();
        if (Objects.nonNull(nonUsdCcy)) {
            spotRateDO.setNonUsdCcy(nonUsdCcy.getCcyCode());
        }
        if (Objects.nonNull(spotRate.getLastUpdateTime())) {
            ZonedDateTime zdt = spotRate.getLastUpdateTime().atZone(ZoneId.systemDefault());
            Date date = Date.from(zdt.toInstant());
            spotRateDO.setUpdatedTime(date);
            spotRateDO.setSnapshotDate(date);
        }
        return spotRateDO;
    }

    public static List<BankQuotation> convertSpotRateToBankQuote(SpotRate spotRate) {
        List<BankQuotation> resultList = Lists.newArrayList();
        for (ExecutingBankEnum bank : ExecutingBankEnum.values()) {
            resultList.add(convertSpotRateToBankQuote(bank, spotRate));
        }
        return resultList;
    }

    public static BankQuotation convertSpotRateToBankQuote(ExecutingBankEnum bank, SpotRate spotRate) {
        if (Objects.nonNull(spotRate.getRate())) {
            BankQuotation bankQuote = new BankQuotation();
            bankQuote.setBankCode(bank.getCode());
            bankQuote.setBankName(bank.getName());

            bankQuote.setBaseCcy(spotRate.getCcyPair().getCcy1().getCcyCode());
            bankQuote.setQuoteCcy(spotRate.getCcyPair().getCcy2().getCcyCode());
            Double discount = RateCalculator.multiply(spotRate.getRateDoubleValue(), RateCalculator.randomDiscountInDouble(0.003));
            bankQuote.setMiddle(spotRate.getRateDoubleValue() + discount);

            bankQuote.setCashBuy(QuotationHelper.getCashBuy(bankQuote).doubleValue());
            bankQuote.setExchangeBuy(QuotationHelper.getExchangeBuy(bankQuote).doubleValue());
            bankQuote.setExchangeSell(QuotationHelper.getExchangeSell(bankQuote).doubleValue());
            bankQuote.setCashSell(QuotationHelper.getCashSell(bankQuote).doubleValue());

            bankQuote.setUpdateTime(LocalDateTime.now());
            return bankQuote;
        }
        return null;
    }


    public static List<BankQuotation> convertBankQuotation(List<BankQuotation> exchangeQuotationList) {
        List<BankQuotation> resultList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(exchangeQuotationList)) {
            exchangeQuotationList.forEach(bankQuotation -> {
                BankQuotation bankQuote = new BankQuotation();
                bankQuote.setQuoteCcy(bankQuotation.getBaseCcy());
                bankQuote.setBaseCcy(bankQuotation.getQuoteCcy());

                bankQuote.setBankCode(bankQuotation.getBankCode());
                bankQuote.setBankName(bankQuotation.getBankName());
                bankQuote.setUpdateTime(bankQuotation.getUpdateTime());

                Double middleRate = RateCalculator.div(1d, bankQuotation.getMiddle());
                bankQuote.setMiddle(middleRate);
                bankQuote.setCashBuy(RateCalculator.div(1d, bankQuotation.getCashBuy()));
                bankQuote.setExchangeBuy(RateCalculator.div(1d, bankQuotation.getExchangeBuy()));
                bankQuote.setExchangeSell(RateCalculator.div(1d, bankQuotation.getExchangeSell()));
                bankQuote.setCashSell(RateCalculator.div(1d, bankQuotation.getCashSell()));
                bankQuote.setCashBuy(QuotationHelper.getCashBuy(bankQuote).doubleValue());
                bankQuote.setExchangeBuy(QuotationHelper.getExchangeBuy(bankQuote).doubleValue());
                bankQuote.setExchangeSell(QuotationHelper.getExchangeSell(bankQuote).doubleValue());
                bankQuote.setCashSell(QuotationHelper.getCashSell(bankQuote).doubleValue());
                resultList.add(bankQuote);
            });
        }
        return resultList;
    }
}
