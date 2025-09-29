package com.luopc.platform.web.mds.init;

import cn.hutool.core.date.StopWatch;
import com.luopc.platform.web.mds.jobs.*;
import com.luopc.platform.web.mds.jobs.*;
import jakarta.annotation.Resource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * @author Robin
 */
@Slf4j
@Setter
@Component
public class ServerInitialRunner implements ApplicationRunner {

    /**
     * 1、corePoolSize线程池的核心线程数
     * 2、maximumPoolSize能容纳的最大线程数
     * 3、keepAliveTime空闲线程存活时间
     * 4、unit 存活的时间单位
     * 5、workQueue 存放提交但未执行任务的队列
     * 6、threadFactory 创建线程的工厂类
     * 7、handler 等待队列满后的拒绝策略
     */
    private final ExecutorService executorService = new ThreadPoolExecutor(2, 5,
            1L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(3),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());

    @Resource
    private BankQuoteRetrievingTask bankQuoteRetrievingTask;
    @Resource
    private MarketSpotRatesRetrievingTask marketSpotRatesRetrievingTask;
    @Resource
    private CcyMappingRetrievingTask currencyMappingRetrievingTask;
    @Resource
    private InterestRatesRetrievingTask interestRatesRetrievingTask;
    @Resource
    private MarketRatesRetrievingTask marketRatesRetrievingTask;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        StopWatch stopWatch = new StopWatch("MDSInitialRunner");
        stopWatch.start("Retrieving BankQuote");
        bankQuoteRetrievingTask.initialLoad();
        stopWatch.stop();
        stopWatch.start("Retrieving MarketRates");
        marketSpotRatesRetrievingTask.initialLoad();
        stopWatch.stop();
        stopWatch.start("Retrieving CurrencyMapping");
        currencyMappingRetrievingTask.initialLoad();
        stopWatch.stop();
        stopWatch.start("Retrieving MarketQuotation");
        marketRatesRetrievingTask.initialLoad();
        stopWatch.stop();
        stopWatch.start("Retrieving InterestRates");
        interestRatesRetrievingTask.initialLoad();
        stopWatch.stop();
        log.info("{}", stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
    }



}
