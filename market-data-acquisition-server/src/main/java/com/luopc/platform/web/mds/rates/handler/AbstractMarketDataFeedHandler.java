package com.luopc.platform.web.mds.rates.handler;

import cn.hutool.core.date.StopWatch;
import com.luopc.platform.common.core.util.GeneratorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Robin
 */
@Slf4j
public abstract class AbstractMarketDataFeedHandler<T> implements MarketDataFeedHandler<T> {

    private final Object processLock = new Object();
    /**
     * Prevent stack overflow
     */
    private final AtomicBoolean initializing = new AtomicBoolean(true);
    private final List<T> messageCache = new ArrayList<>();

    @Override
    public void onInitialLoad(Collection<T> initialList) {
        String uuid = GeneratorUtil.shortUuid();
        StopWatch stopWatch = new StopWatch("initialLoad[" + uuid + "]");
        synchronized (processLock) {
            stopWatch.start("process initialLoad message for job[" + uuid + "]");
            log.info("Start to process initialLoad message for job[{}], size = {}", uuid, initialList.size());
            if (CollectionUtils.isNotEmpty(initialList)) {
                processInitialLoadMsg(new ArrayList<>(initialList));
                log.info("Completed to process initialLoad message for job[{}], size = {}", uuid, initialList.size());
            }
            stopWatch.stop();
            stopWatch.start("process cache message for job[" + uuid + "]");
            if (CollectionUtils.isNotEmpty(messageCache)) {
                processMessage(messageCache);
                log.info("Completed to process cache message for job[{}], size = {}", uuid, messageCache.size());
            }
            initializing.compareAndSet(true, false);
            stopWatch.stop();
            if (log.isDebugEnabled()) {
                log.debug("{}", stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
            } else {
                log.info("Job[{}] has been initialized, running time = {} ms", uuid, stopWatch.getTotalTimeMillis());
            }
        }
    }

    @Override
    public void onResponse(Collection<T> responseList) {
        if (initializing.get()) {
            synchronized (processLock) {
                if (initializing.get()) {
                    log.info("Add {} to cache during initialLoading", responseList.size());
                    messageCache.addAll(responseList);
                } else {
                    processMessage(responseList);
                }
            }
        } else {
            processMessage(responseList);
        }
    }

    /**
     * process initial load msg
     *
     * @param initialList init
     */
    protected abstract void processInitialLoadMsg(Collection<T> initialList);

    /**
     * process response  msg
     *
     * @param responseList response
     */
    protected abstract void processMessage(Collection<T> responseList);


}
