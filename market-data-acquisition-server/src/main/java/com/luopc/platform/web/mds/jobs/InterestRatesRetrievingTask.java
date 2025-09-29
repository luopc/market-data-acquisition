package com.luopc.platform.web.mds.jobs;

import com.luopc.platform.web.mds.convertors.MapStructConvertor;
import com.luopc.platform.web.mds.jobs.rates.service.InterestRatesRetrievingService;
import com.luopc.platform.web.mds.rates.domain.entity.CcyInterestDO;
import com.luopc.platform.web.mds.rates.handler.impl.InterestRatesFeedHandler;
import com.luopc.platform.web.mds.rates.mappers.CcyInterestMapper;
import jakarta.annotation.Resource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Robin
 * @description 更新各国利率数据，用于计算forward rates
 */
@Slf4j
@Setter
@Component
public class InterestRatesRetrievingTask {

    @Resource
    private CcyInterestMapper ccyInterestMapper;
    @Resource
    private MapStructConvertor mapStructConvertor;
    @Resource
    private InterestRatesFeedHandler interestRatesFeedHandler;
    @Resource
    private InterestRatesRetrievingService interestRatesRetrievingService;

    public void initialLoad() {
        List<CcyInterestDO> ccyInterestList = ccyInterestMapper.initialLoad();
        interestRatesFeedHandler.onInitialLoad(ccyInterestList);
    }

    /**
     * initialDelay: 初始延迟。任务的第一次执行将延迟5秒，然后将以固定间隔执行。
     * Retrieve Interest rate from
     * api.interestRateUrl=<a href="https://zh.tradingeconomics.com/country-list/interest-rate?continent=">各国利率数据</a>
     * api.interestContinents=all,europe,america,asia,africa,australia,g20
     */
    @Async
    @Scheduled(initialDelay = 15 * 1000, fixedDelay = 8 * 60 * 60 * 1000)
    public void updateInterestRates() {
        log.info("[RetrievingInterestJob][定时任务每八小时执行：{}]", LocalDateTime.now());
        interestRatesFeedHandler.onResponse(interestRatesRetrievingService.getInterestRates());
    }


}
