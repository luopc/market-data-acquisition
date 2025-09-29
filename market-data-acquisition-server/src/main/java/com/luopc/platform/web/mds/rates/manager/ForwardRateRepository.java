package com.luopc.platform.web.mds.rates.manager;

import cn.hutool.core.map.MapUtil;
import com.google.common.collect.Lists;
import com.luopc.platform.common.core.util.SmartRandomUtil;
import com.luopc.platform.market.holiday.HolidayRepository;
import com.luopc.platform.market.api.Currency;
import com.luopc.platform.market.api.*;
import com.luopc.platform.market.tools.RateCalculator;
import com.luopc.platform.web.mds.handler.event.SpotRateEvent;
import com.luopc.platform.web.mds.rates.domain.entity.CcyInterestDO;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author by Robin
 * @className ForwardRateRepository
 * @description TODO
 * @date 2024/1/5 0005 22:13
 */
@Slf4j
@Setter
@Service
public class ForwardRateRepository {


    private final Map<CcyPair, ForwardRate> forwardRatesCacheOfCcyPair = new ConcurrentHashMap<>();

    @Resource
    private InterestRatesRepository interestRatesRepository;

    @Async
    @EventListener(value = SpotRateEvent.class)
    public void handleSpotRateEvent(SpotRateEvent spotRateEvent) {
        log.info("Receive SpotRateEvent message, {}", spotRateEvent.getChangeList().size());
        List<SpotRate> spotRateList = spotRateEvent.getChangeList();
        this.updateForwardRateCache(spotRateList);
    }

    public void updateForwardRateCache(List<SpotRate> spotRateList) {
        Optional.ofNullable(spotRateList).orElse(Lists.newArrayList()).forEach(spotRate -> {
            Currency ccy1 = spotRate.getCcyPair().getCcy1();
            Currency ccy2 = spotRate.getCcyPair().getCcy2();
            BigDecimal rate = spotRate.getRate();
            CcyInterestDO ccy1Interest = interestRatesRepository.getInterestRate(ccy1);
            CcyInterestDO ccy2Interest = interestRatesRepository.getInterestRate(ccy2);
            if (Objects.nonNull(rate) && Objects.nonNull(ccy1Interest) && Objects.nonNull(ccy2Interest)) {
                log.debug("[{}]SpotRate = {}, ccy1Interest={}, ccy2Interest={}", spotRate.getCcyPair().getCcyPairStr(), rate.doubleValue(), ccy1Interest.getRate(), ccy2Interest.getRate());
                Map<Tenor, Double> tenorRatesResult = RateCalculator.calculateForwardPoints(rate.doubleValue(), ccy1Interest.getRate(), ccy2Interest.getRate());
                StringJoiner tenors1 = new StringJoiner(",");
                tenorRatesResult.forEach((k, v) -> {
                    tenors1.add(String.format("%s:%.4f", k.getCode(), v));
                });
                log.debug("{}", String.format("%s, tenors=%s%n", spotRate.getCcyPair().getCcyPairStr(), tenors1));

                List<Interpolator> interpolatorList = new ArrayList<>(tenorRatesResult.size());
                tenorRatesResult.forEach((k, v) -> {
                    Interpolator interpolator = new Interpolator(HolidayRepository.getNextNearBusinessDate(LocalDate.now()), k, v);
                    interpolatorList.add(interpolator);
                });

                ForwardRate forwardRate = new ForwardRate(spotRate.getCcyPair(), Math.max(ccy1.getAmtDecimal(), ccy2.getAmtDecimal()), interpolatorList, spotRate.getLastUpdateTime());
                forwardRatesCacheOfCcyPair.put(spotRate.getCcyPair(), forwardRate);
            }
        });
    }

    public ForwardRate randomGet() {
        return SmartRandomUtil.randomGet(new ArrayList<>(forwardRatesCacheOfCcyPair.values()));
    }

    public Map<CcyPair, ForwardRate> getAllForwardRates() {
        return MapUtil.unmodifiable(forwardRatesCacheOfCcyPair);
    }

    public ForwardRate getForwardRate(CcyPair ccyPair) {
        return forwardRatesCacheOfCcyPair.get(ccyPair);
    }

    public Set<CcyPair> getCcyPairs() {
        return forwardRatesCacheOfCcyPair.keySet();
    }
}
