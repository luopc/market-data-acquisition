package com.luopc.platform.web.mds.rates.domain.dto;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.luopc.platform.market.api.*;
import com.luopc.platform.market.holiday.HolidayRepository;

import java.time.LocalDate;

/**
 * @author by Robin
 * @className BankQuoteByTenor
 * @date 2024/1/4 0004 22:07
 */
public class BankQuoteByTenor {

    private final String bankCode;

    private final Table<String, String, QuoteByTenor> exchangeQuoteByTenor = HashBasedTable.create();

    public BankQuoteByTenor(String bankCode) {
        this.bankCode = bankCode;
    }

    public void update(Tenor tenor, CcyPair ccyPair, QuoteByTenor quote) {
        exchangeQuoteByTenor.put(ccyPair.getCcyPairStr(), tenor.getCode(), quote);
    }

    public QuoteByTenor get(CcyPair ccyPair, LocalDate valueDate) {
        return get(ccyPair, HolidayRepository.getTenorByValueDate(valueDate));
    }

    public QuoteByTenor get(CcyPair ccyPair, Tenor tenor) {
        return exchangeQuoteByTenor.get(ccyPair.getCcyPairStr(), tenor.getCode());
    }
}
