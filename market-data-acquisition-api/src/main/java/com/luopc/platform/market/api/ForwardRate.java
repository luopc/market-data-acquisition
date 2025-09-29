package com.luopc.platform.market.api;

import com.luopc.platform.market.holiday.HolidayRepository;
import com.luopc.platform.market.tools.RateCalculator;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author Robin
 */
@Data
@Slf4j
public class ForwardRate implements Rate {

    public static final int FROM_FACTOR = 10000;

    private final CcyPair ccyPair;
    private final Map<Tenor, Interpolator> interpolatorMap = new TreeMap<>();
    private final int decimalPlaces;
    private LocalDateTime lastUpdateTime;

    public ForwardRate(CcyPair ccyPair, int decimalPlaces) {
        this.ccyPair = ccyPair;
        this.decimalPlaces = decimalPlaces;
    }

    public ForwardRate(CcyPair ccyPair, int decimalPlaces, List<Interpolator> interpolatorList, LocalDateTime lastUpdateTime) {
        this(ccyPair, decimalPlaces);
        this.lastUpdateTime = lastUpdateTime;
        interpolatorList.forEach(this::addInterpolator);
    }

    public void addInterpolator(Interpolator interpolator) {
        interpolatorMap.put(interpolator.getTenor(), interpolator);
    }

    public BigDecimal getForwardRate(SpotRate spotRate, LocalDate valueDate) {
        if (CollectionUtils.isNotEmpty(interpolatorMap.values())) {
            return RateCalculator.sum(spotRate.getRate(), RateCalculator.div(getForwardPoint(valueDate), BigDecimal.valueOf(FROM_FACTOR)), decimalPlaces);
        } else {
            return spotRate.getRate();
        }
    }

    private BigDecimal getForwardPoint(LocalDate valueDate) {
        Map<Integer, Double> forwardPointCurve = interpolatorMap.values().stream().collect(Collectors.toMap(Interpolator::getDaysFromToday, Interpolator::getPoint));
        List<Integer> tenorDayList = new ArrayList<>(forwardPointCurve.keySet()).stream().sorted().collect(Collectors.toList());

        int valueDateFromDay = HolidayRepository.getWorkingDates(LocalDate.now(), HolidayRepository.getNextNearBusinessDate(valueDate));
        Integer baseDays = tenorDayList.get(0);
        if (baseDays >= valueDateFromDay) {
            return RateCalculator.multiply(BigDecimal.valueOf(forwardPointCurve.get(baseDays)), BigDecimal.ONE);
        } else {
            Double basePoint = forwardPointCurve.get(baseDays);
            for (Integer tenorDays : tenorDayList) {
                if (tenorDays >= valueDateFromDay) {
                    Double tenorPoint = forwardPointCurve.get(tenorDays);
                    return calculateForwardPoint(basePoint, tenorPoint, baseDays, tenorDays, valueDateFromDay);
                }
                baseDays = tenorDays;
                basePoint = forwardPointCurve.get(baseDays);
            }
            /*
             *if valueDate larger than all existing tenor and tenor >=2
             */
            if (interpolatorMap.size() >= 2) {
                tenorDayList = tenorDayList.stream().sorted().distinct().collect(Collectors.toList());
                Integer longestTenorDays = tenorDayList.get(0);
                Double tenorPoint = forwardPointCurve.get(longestTenorDays);
                baseDays = tenorDayList.get(1);
                basePoint = forwardPointCurve.get(baseDays);
                double result = basePoint + (tenorPoint - basePoint) * (valueDateFromDay - longestTenorDays) / (longestTenorDays - baseDays);
                return RateCalculator.multiply(BigDecimal.valueOf(result), BigDecimal.ONE);
            }
        }
        log.error("Unable to calculate forward rate for {}, forwardPointCurve: {}", ccyPair.getCcyPairStr(), forwardPointCurve);
        return BigDecimal.ZERO;
    }

    /**
     * Formula = basePoint + (tenorPoint-basePoint) * [(valueDateFromDay-baseDays)/(tenorDays-baseDays)]
     *
     * @param basePoint        Previous Point
     * @param tenorPoint       point of current Tenor Date
     * @param baseDays         Previous Days
     * @param tenorDays        Days of current Tenor Date
     * @param valueDateFromDay Value Date From today
     * @return ForwardPoint
     */
    private BigDecimal calculateForwardPoint(Double basePoint, Double tenorPoint, Integer baseDays, Integer tenorDays, Integer valueDateFromDay) {
        double result = basePoint + (tenorPoint - basePoint) * (valueDateFromDay - baseDays) / (tenorDays - baseDays);
        return RateCalculator.multiply(BigDecimal.valueOf(result), BigDecimal.ONE);
    }

}

