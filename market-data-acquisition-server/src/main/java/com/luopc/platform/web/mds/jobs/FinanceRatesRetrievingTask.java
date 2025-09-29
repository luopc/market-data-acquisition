package com.luopc.platform.web.mds.jobs;

import com.luopc.platform.market.api.CcyPair;
import com.luopc.platform.market.api.SpotRate;
import com.luopc.platform.web.mds.common.TradeMainCcyPairHelper;
import com.luopc.platform.web.mds.jobs.rates.api.FinanceRatesApiCallService;
import com.luopc.platform.web.mds.rates.handler.impl.SpotRatesFeedHandler;
import jakarta.annotation.Resource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;

/**
 * @author Robin
 * @description 更新货币兑换汇率
 */
@Slf4j
@Setter
@Component
public class FinanceRatesRetrievingTask {

    private final static int RATE_WAIT_IN_HOURS = 2;

    @Resource
    private SpotRatesFeedHandler spotRatesFeedHandler;
    @Resource
    private TradeMainCcyPairHelper tradeMainCcyPairHelper;
    @Resource
    private FinanceRatesApiCallService financeRatesApiCallService;

    @Async
    @Scheduled(initialDelay = 15 * 1000, fixedDelay = 60 * 60 * 1000 * RATE_WAIT_IN_HOURS)
    public void retrieveFinanceRates() {
        CcyPair ccyPair = tradeMainCcyPairHelper.getCcyPair();
        log.info("[retrieveFinanceRates][定时任务每{}小时执行：{}], CcyPair = {}", RATE_WAIT_IN_HOURS, LocalDateTime.now(), ccyPair);
        if (Objects.nonNull(ccyPair)) {
            SpotRate spotRate = financeRatesApiCallService.getSpotRateFromApi(ccyPair.getCcy1(), ccyPair.getCcy2());
            if (Objects.nonNull(spotRate)) {
                spotRatesFeedHandler.onResponse(Collections.singletonList(spotRate));
            }
        }
    }


}
