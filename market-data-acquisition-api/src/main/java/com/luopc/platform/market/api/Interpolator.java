package com.luopc.platform.market.api;

import com.luopc.platform.market.holiday.HolidayRepository;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Robin
 */
@Data
public class Interpolator {

    private final LocalDate businessDay;
    private final LocalDate tenorDate;
    private final Tenor tenor;
    private final Integer daysFromToday;
    private final Double point;
    private LocalDateTime updateTime;

    public Interpolator(LocalDate businessDay, Tenor tenor, Double point) {
        this.tenor = tenor;
        this.point = point;
        this.businessDay = businessDay;
        this.tenorDate = HolidayRepository.getValueDateByTenor(businessDay, tenor);
        this.daysFromToday = tenor.getBusinessDays();
    }

    public Interpolator(LocalDate businessDay, Tenor tenor, Double point, LocalDateTime updateTime) {
        this(businessDay, tenor, point);
        this.updateTime = updateTime;
    }

}
