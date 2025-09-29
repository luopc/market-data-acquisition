package com.luopc.platform.web.mds.jobs.bank.dto;

import cn.hutool.core.bean.BeanUtil;
import com.luopc.platform.market.api.ExecutingBankEnum;
import com.luopc.platform.market.tools.RateCalculator;
import com.luopc.platform.web.mds.jobs.common.response.ResponseMessage;
import com.luopc.platform.web.mds.rates.domain.dto.BankQuotation;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Robin
 */
@Data
public class QuoteMessage implements ResponseMessage {

    private String success;
    private String baseCcy;
    private String quoteCcy;
    private Map<String, QuoteInfo> result;

    @Override
    public boolean isSuccess() {
        return "1".equals(success);
    }

    public List<BankQuotation> getBankExchangeQuotationList() {
        List<BankQuotation> quotationList = new ArrayList<>();
        if (result != null && !result.isEmpty()) {
            result.values().forEach(info -> {
                quotationList.add(convertToExchangeQuotation(info.getBOC()));
                quotationList.add(convertToExchangeQuotation(info.getCCB()));
                quotationList.add(convertToExchangeQuotation(info.getICBC()));
                quotationList.add(convertToExchangeQuotation(info.getABC()));
                quotationList.add(convertToExchangeQuotation(info.getCEB()));
            });
        }
        return quotationList.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private BankQuotation convertToExchangeQuotation(BankQuoteInfo bankQuoteInfo) {
        BankQuotation exchangeQuotation = new BankQuotation();
        if (bankQuoteInfo != null) {
            BeanUtil.copyProperties(bankQuoteInfo, exchangeQuotation);
            String bankCode = bankQuoteInfo.getBankCode();
            ExecutingBankEnum executingBankEnum = convertToExecutingBank(bankCode);
            exchangeQuotation.setBankCode(executingBankEnum.getCode());
            exchangeQuotation.setBankName(executingBankEnum.getName());
            exchangeQuotation.setBaseCcy(baseCcy);
            exchangeQuotation.setQuoteCcy(quoteCcy);
            exchangeQuotation.setExchangeBuy(RateCalculator.div(bankQuoteInfo.getExchangeBuy(), 100d, RateCalculator.RATE_SCALE));
            exchangeQuotation.setExchangeSell(RateCalculator.div(bankQuoteInfo.getExchangeSell(), 100d, RateCalculator.RATE_SCALE));
            exchangeQuotation.setCashBuy(RateCalculator.div(bankQuoteInfo.getCashSell(), 100d, RateCalculator.RATE_SCALE));
            exchangeQuotation.setCashSell(RateCalculator.div(bankQuoteInfo.getCashSell(), 100d, RateCalculator.RATE_SCALE));
            exchangeQuotation.setMiddle(RateCalculator.div(bankQuoteInfo.getMiddle(), 100d, RateCalculator.RATE_SCALE));
            if (Objects.nonNull(bankQuoteInfo.getUpddate())) {
                exchangeQuotation.setUpdateTime(LocalDateTime.ofInstant(bankQuoteInfo.getUpddate().toInstant(), ZoneId.systemDefault()));
            }
            return exchangeQuotation;
        }
        return null;
    }

    private ExecutingBankEnum convertToExecutingBank(String bankCode) {
        if ("BOC".equalsIgnoreCase(bankCode)) {
            return ExecutingBankEnum.BOCC;
        } else if ("CCB".equalsIgnoreCase(bankCode)) {
            return ExecutingBankEnum.CCBC;
        } else if ("ICBC".equalsIgnoreCase(bankCode)) {
            return ExecutingBankEnum.ICBC;
        } else if ("ABC".equalsIgnoreCase(bankCode)) {
            return ExecutingBankEnum.ABCC;
        } else if ("CEB".equalsIgnoreCase(bankCode)) {
            return ExecutingBankEnum.CEBB;
        } else {
            return ExecutingBankEnum.BOCC;
        }

    }

    public List<BankQuoteInfo> getBankQuoteList() {
        List<BankQuoteInfo> bankQuoteInfoList = new ArrayList<>();
        if (result != null && !result.isEmpty()) {
            result.values().forEach(info -> {
                bankQuoteInfoList.add(updateBankQuote(info.getBOC()));
                bankQuoteInfoList.add(updateBankQuote(info.getCCB()));
                bankQuoteInfoList.add(updateBankQuote(info.getICBC()));
                bankQuoteInfoList.add(updateBankQuote(info.getABC()));
                bankQuoteInfoList.add(updateBankQuote(info.getCEB()));
            });
        }
        return bankQuoteInfoList.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private BankQuoteInfo updateBankQuote(BankQuoteInfo bankQuoteInfo) {
        if (bankQuoteInfo != null) {
            bankQuoteInfo.setBaseCcy(baseCcy);
            bankQuoteInfo.setQuoteCcy(quoteCcy);
            bankQuoteInfo.setUpddate(bankQuoteInfo.getUpddate());
            return bankQuoteInfo;
        }
        return null;
    }

}
