package com.luopc.platform.market.api;

import com.luopc.platform.market.holiday.HolidayRepository;
import lombok.Getter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Robin
 */

@Getter
public enum Tenor {


    /**
     * ON: Over-Night隔夜，即当天起息，次日交割
     * TN: Tom-Next，即次日（Tom就是Tomorrow）起息，第三日交割
     * SN: Spot-Next，即期起息（即期的天数随币种而不一样，多数为2天），即期的次日交割
     * FWD: Forward
     */
    TDY("TDY", 0, ChronoUnit.DAYS, "Today", 2),
    TOM("TOM", 1, ChronoUnit.DAYS, "Tomorrow", 3),
    SPOT("SPOT", 2, ChronoUnit.DAYS, "Spot(T+2)", 15),
    FWD_1WK("1W", 1, ChronoUnit.WEEKS, "1 Week", 50),
    FWD_2WK("2W", 2, ChronoUnit.WEEKS, "2 Weeks", 40),
    FWD_1MO("1M", 1, ChronoUnit.MONTHS, "1 Month", 30),
    FWD_2MO("2M", 2, ChronoUnit.MONTHS, "2 Months", 20),
    FWD_3MO("3M", 3, ChronoUnit.MONTHS, "3 Months", 10),
    FWD_6MO("6M", 6, ChronoUnit.MONTHS, "6 Months", 10),
    FWD_18MO("18M", 18, ChronoUnit.MONTHS, "18 Months", 8),
    FWD_1Y("1Y", 1, ChronoUnit.YEARS, "1 Year", 7),
    FWD_2Y("2Y", 2, ChronoUnit.YEARS, "2 Years", 5),
    FWD_3Y("3Y", 3, ChronoUnit.YEARS, "3 Years", 4),
    FWD_5Y("5Y", 4, ChronoUnit.YEARS, "5 Years", 3),
    FWD_10Y("10Y", 10, ChronoUnit.YEARS, "10 Years", 2),
    FWD_15Y("15Y", 15, ChronoUnit.YEARS, "15 Years", 1),
    ;

    private final String code;
    private final Integer unit;
    private final ChronoUnit timeUnit;
    private final String description;
    /**
     * 用于计算落入此交易日期的比例
     */
    private final int weight;
    private final static Map<String, Tenor> TENOR_BUCKET_MAP = Arrays.stream(Tenor.values()).collect(Collectors.toMap(Tenor::getCode, t -> t));

    Tenor(String code, Integer unit, ChronoUnit timeUnit, String description, int weight) {
        this.code = code;
        this.unit = unit;
        this.timeUnit = timeUnit;
        this.description = description;
        this.weight = weight;
    }

    public static Tenor getByCode(String tenor) {
        return TENOR_BUCKET_MAP.get(tenor);
    }

    public Integer getBusinessDays() {
        return HolidayRepository.getWorkingDates(LocalDate.now(), HolidayRepository.getValueDateByTenor(this));
    }

    public Integer getDays() {
        LocalDate today = LocalDate.now();
        return switch (timeUnit) {
            case WEEKS -> (int) today.until(today.plusWeeks(unit), ChronoUnit.DAYS);
            case MONTHS -> (int) today.until(today.plusMonths(unit), ChronoUnit.DAYS);
            case YEARS -> (int) today.until(today.plusYears(unit), ChronoUnit.DAYS);
            default -> unit;
        };
    }
}
