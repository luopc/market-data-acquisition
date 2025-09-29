package com.luopc.platform.web.mds.jobs.bank.api;

import com.alibaba.fastjson2.JSON;
import com.luopc.platform.web.mds.jobs.common.NowApiCallService;
import com.luopc.platform.web.mds.jobs.bank.dto.QuoteMessage;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author by Robin
 * @className NowApiBankQuoteService
 * @date 2024/1/6 0006 10:28
 */
@Slf4j
@Setter
@Service
@NoArgsConstructor
public class NowApiBankQuoteService extends NowApiCallService {


    public QuoteMessage retrieveBankQuote(String ccy) {
        String url = getNowApiRateCNYQuotUrl(ccy);
        log.info("Quote Request url: {}", url);
        QuoteMessage message = new QuoteMessage();
        if (isNotDev()) {
            String result = getData(url);
            message = JSON.parseObject(result, QuoteMessage.class);
            message.setBaseCcy(ccy);
            message.setQuoteCcy("CNY");
            message.getBankQuoteList().forEach(bankQuoteInfo -> {
                bankQuoteInfo.setBaseCcy(ccy);
                bankQuoteInfo.setQuoteCcy("CNY");
            });
        }
        return message;
    }


    private String getNowApiRateCNYQuotUrl(String ccy) {
        return getNowApiURIBuilder()
                .queryParam("app", economicsApiConfig.getNowapiFinanceRateCnyquot())
                .queryParam("curno", ccy).toUriString();
    }
}
