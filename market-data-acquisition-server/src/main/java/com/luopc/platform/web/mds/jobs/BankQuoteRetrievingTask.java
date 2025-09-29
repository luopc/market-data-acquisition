package com.luopc.platform.web.mds.jobs;

import com.google.common.collect.Lists;
import com.luopc.platform.market.api.CcyPair;
import com.luopc.platform.web.mds.common.TradeMainCcyPairHelper;
import com.luopc.platform.web.mds.convertors.MapStructConvertor;
import com.luopc.platform.web.mds.jobs.bank.api.IBankDataApiCallService;
import com.luopc.platform.web.mds.jobs.bank.service.BankQuoteRetrievingService;
import com.luopc.platform.web.mds.rates.domain.dto.BankQuotation;
import com.luopc.platform.web.mds.rates.domain.entity.BankQuotationDO;
import com.luopc.platform.web.mds.rates.handler.impl.BankQuotationFeedHandler;
import com.luopc.platform.web.mds.rates.mappers.BankQuotationMapper;
import jakarta.annotation.Resource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author by Robin
 * @className CurrencyRatesRetrieveService
 * @description 更新各大银行的货币兑换汇率
 * @date 2024/1/6 0006 10:44
 */
@Slf4j
@Setter
@Component
public class BankQuoteRetrievingTask {

    private final static int RATE_WAIT_IN_MINUTES = 25;

    @Resource
    private MapStructConvertor mapStructConvertor;
    @Resource
    private BankQuotationMapper bankQuotationMapper;
    @Resource
    private TradeMainCcyPairHelper tradeMainCcyPairHelper;
    @Resource
    private IBankDataApiCallService iBankDataApiCallService;
    @Resource
    private BankQuotationFeedHandler bankQuotationFeedHandler;
    @Resource
    private BankQuoteRetrievingService bankQuoteRetrievingService;

    public void initialLoad() {
        List<BankQuotationDO> bankQuotationFromDataBase = bankQuotationMapper.initialLoad();
        List<BankQuotation> bankQuotationList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(bankQuotationFromDataBase)) {
            bankQuotationList = bankQuotationFromDataBase.stream().map(quoteDO -> mapStructConvertor.doToBankQuotation(quoteDO)).collect(Collectors.toList());
        }
        bankQuotationFeedHandler.onInitialLoad(bankQuotationList);
    }

    @Scheduled(initialDelay = 15 * 1000, fixedDelay = 60 * 1000 * RATE_WAIT_IN_MINUTES)
    public void retrieveBankQuotation() {
        CcyPair ccyPair = tradeMainCcyPairHelper.getCcyPair();
        log.info("[retrieveBankQuotation][定时任务每{}分钟执行：{}], CcyPair = {}", RATE_WAIT_IN_MINUTES, LocalDateTime.now(), ccyPair);
        List<BankQuotation> exchangeQuotationList = Lists.newArrayList();
        if (Objects.nonNull(ccyPair)) {
            exchangeQuotationList.addAll(bankQuoteRetrievingService.getQuoteFromApi(ccyPair.getCcy1()));
            exchangeQuotationList.addAll(bankQuoteRetrievingService.getQuoteFromApi(ccyPair.getCcy2()));
        }
        //exchangeQuotationList.addAll(iBankDataApiCallService.getExchangeQuotationFromAPI());
        bankQuotationFeedHandler.onResponse(exchangeQuotationList);
    }


}
