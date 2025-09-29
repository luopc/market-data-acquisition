package com.luopc.platform.web.mds.restful.service;

import com.luopc.platform.common.core.exception.BusinessException;
import com.luopc.platform.market.api.CcyPair;
import com.luopc.platform.market.api.Currency;
import com.luopc.platform.market.api.Tenor;
import com.luopc.platform.web.mds.convertors.MarketDataConvertor;
import com.luopc.platform.web.mds.rates.domain.dto.BankQuotation;
import com.luopc.platform.web.mds.rates.domain.dto.QuoteByTenor;
import com.luopc.platform.web.mds.restful.domain.MDSErrorCode;
import com.luopc.platform.web.mds.restful.domain.from.QuoteQueryForm;
import com.luopc.platform.web.mds.restful.domain.vo.QuoteVO;
import com.luopc.platform.web.mds.restful.manager.MarketDataRepository;
import com.luopc.platform.web.result.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Robin
 */
@Slf4j
@Service
public class QuoteService {

    @Resource
    private MarketDataRepository marketDataRepository;

    public ResponseEntity<List<QuoteVO>> queryBankQuote(QuoteQueryForm queryForm) {
        Currency baseCcy = Currency.getInstance(queryForm.getBaseCcy());
        Currency quoteCcy = Currency.getInstance(queryForm.getQuoteCcy());
        if (StringUtils.isNoneBlank(queryForm.getTenor())) {
            Tenor tenor = Tenor.getByCode(queryForm.getTenor());
            List<QuoteByTenor> bankQuotesForTenor = marketDataRepository.getQuoteListByCcyPairAndTenor(CcyPair.getInstance(baseCcy, quoteCcy), tenor);
            return getQuoteListResponseByTenor(bankQuotesForTenor, baseCcy, quoteCcy);
        } else if (StringUtils.isNoneBlank(queryForm.getValueDate())) {
            LocalDate valueDate = LocalDate.parse(queryForm.getValueDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            List<QuoteByTenor> bankQuotesForTenor = marketDataRepository.getQuoteListByCcyPairAndValueDate(CcyPair.getInstance(baseCcy, quoteCcy), valueDate);
            return getQuoteListResponseByTenor(bankQuotesForTenor, baseCcy, quoteCcy);
        } else {
            List<BankQuotation> bankQuotesForCcy = marketDataRepository.getBankQuote(CcyPair.getInstance(baseCcy, quoteCcy));
            if (CollectionUtils.isNotEmpty(bankQuotesForCcy)) {
                List<QuoteVO> quoteVos = bankQuotesForCcy.stream().map(MarketDataConvertor::convertToQuoteVO).collect(Collectors.toList());
                return ResponseEntity.ok(quoteVos);
            } else {
                throw new BusinessException(MDSErrorCode.QUOTE_CANNOT_FOUND);
            }
        }
    }

    private ResponseEntity<List<QuoteVO>> getQuoteListResponseByTenor(List<QuoteByTenor> bankQuotesForTenor, Currency baseCcy, Currency quoteCcy) {
        if (CollectionUtils.isNotEmpty(bankQuotesForTenor)) {
            List<QuoteVO> quoteVos = bankQuotesForTenor.stream().map(quotesForTenor -> {
                QuoteVO quoteVO = MarketDataConvertor.convertToQuoteVO(quotesForTenor);
                quoteVO.setBaseCcy(baseCcy.getCcyCode());
                quoteVO.setQuoteCcy(quoteCcy.getCcyCode());
                return quoteVO;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(quoteVos);
        } else {
            throw new BusinessException(MDSErrorCode.RATE_CANNOT_FOUND);
        }
    }


}
