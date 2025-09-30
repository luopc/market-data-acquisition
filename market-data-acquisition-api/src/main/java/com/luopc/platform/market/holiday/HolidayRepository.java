package com.luopc.platform.market.holiday;

import com.luopc.platform.market.api.Tenor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author by Robin
 * @className HolidayRepository
 * @date 2024/1/4 0004 23:10
 */
@Setter
public class HolidayRepository {

    private static Set<String> holidays = Arrays.stream(new String[]{"01-01", "12-25"}).collect(Collectors.toSet());

    public static Tenor getTenorByValueDate(LocalDate valueDate) {
        if (valueDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("This is a pasted value date: [" + valueDate + "]");
        }
        int days = getWorkingDates(LocalDate.now(), getNextNearBusinessDate(valueDate));
        Map<Integer, Tenor> tenorByWorkingDates = Arrays.stream(Tenor.values()).collect(Collectors.toMap(Tenor::getBusinessDays, k -> k));
        List<Integer> tenorDayList = new ArrayList<>(tenorByWorkingDates.keySet()).stream().sorted().collect(Collectors.toList());
        for (Integer tenorDays : tenorDayList) {
            if (tenorDays >= days) {
                return tenorByWorkingDates.get(tenorDays);
            }
        }
        //get the MAX Tenor
        return tenorByWorkingDates.get(tenorDayList.get(tenorDayList.size() - 1));
    }

    public static LocalDate getValueDateByTenor(Tenor tenor) {
        return getValueDateByTenor(LocalDate.now(), tenor);
    }

    public static LocalDate getValueDateByTenor(LocalDate valueDate, Tenor tenor) {
        switch (tenor.getTimeUnit()) {
            case WEEKS:
                return addWeeksSkippingHoliday(valueDate, tenor.getUnit());
            case MONTHS:
                return addMonthsSkippingHoliday(valueDate, tenor.getUnit());
            case YEARS:
                return addYearsSkippingHoliday(valueDate, tenor.getUnit());
            default:
                return addDaysSkippingHoliday(valueDate, tenor.getUnit());
        }
    }

    public static LocalDate addDaysSkippingHoliday(LocalDate date, int days) {
        LocalDate result = getNextNearBusinessDate(date);
        if (days == 0) {
            return result;
        } else if (days < 0) {
            return subtractDaysSkippingHoliday(date, Math.abs(days));
        } else {
            for (int i = 0; i < days; i++) {
                result = getNextNearBusinessDate(result.plusDays(1));
            }
        }
        return result;
    }

    public static LocalDate addWeeksSkippingHoliday(LocalDate date, int weeks) {
        LocalDate result = getNextNearBusinessDate(date);
        if (weeks == 0) {
            return result;
        } else if (weeks < 0) {
            return subtractWeeksSkippingHoliday(date, Math.abs(weeks));
        } else {
            for (int i = 0; i < weeks; i++) {
                result = getNextNearBusinessDate(result.plusWeeks(1));
            }
        }
        return result;
    }

    public static LocalDate addMonthsSkippingHoliday(LocalDate date, int months) {
        LocalDate result = getNextNearBusinessDate(date);
        if (months == 0) {
            return result;
        } else if (months < 0) {
            return subtractMonthsSkippingHoliday(date, Math.abs(months));
        } else {
            for (int i = 0; i < months; i++) {
                result = getNextNearBusinessDate(result.plusMonths(1));
            }
        }
        return result;
    }

    public static LocalDate addYearsSkippingHoliday(LocalDate date, int years) {
        LocalDate result = getNextNearBusinessDate(date);
        if (years == 0) {
            return result;
        } else if (years < 0) {
            return subtractYearsSkippingHoliday(date, Math.abs(years));
        } else {
            for (int i = 0; i < years; i++) {
                result = getNextNearBusinessDate(result.plusYears(1));
            }
        }
        return result;
    }

    public static LocalDate getNextNearBusinessDate(LocalDate date) {
        LocalDate result = date;
        while (isHoliday(result)) {
            result = result.plusDays(1);
        }
        return result;
    }

    public static LocalDate subtractYearsSkippingHoliday(LocalDate date, int years) {
        LocalDate result = date;
        if (years == 0) {
            return getPreviousBusinessDate(date);
        } else if (years < 0) {
            return addYearsSkippingHoliday(date, Math.abs(years));
        } else {
            for (int i = 0; i < years; i++) {
                result = getPreviousBusinessDate(result.minusYears(1));
            }
        }
        return result;
    }

    public static LocalDate subtractMonthsSkippingHoliday(LocalDate date, int month) {
        LocalDate result = date;
        if (month == 0) {
            return getPreviousBusinessDate(date);
        } else if (month < 0) {
            return addMonthsSkippingHoliday(date, Math.abs(month));
        } else {
            for (int i = 0; i < month; i++) {
                result = getPreviousBusinessDate(result.minusMonths(1));
            }
        }
        return result;
    }

    public static LocalDate subtractWeeksSkippingHoliday(LocalDate date, int weeks) {
        LocalDate result = date;
        if (weeks == 0) {
            return getPreviousBusinessDate(date);
        } else if (weeks < 0) {
            return addWeeksSkippingHoliday(date, Math.abs(weeks));
        } else {
            for (int i = 0; i < weeks; i++) {
                result = getPreviousBusinessDate(result.minusWeeks(1));
            }
        }
        return result;
    }

    public static LocalDate subtractDaysSkippingHoliday(LocalDate date, int days) {
        LocalDate result = getPreviousBusinessDate(date);
        if (days == 0) {
            return result;
        } else if (days < 0) {
            return addDaysSkippingHoliday(date, Math.abs(days));
        } else {
            for (int i = 0; i < days; i++) {
                result = getPreviousBusinessDate(result.minusDays(1));
            }
        }
        return result;
    }

    public static LocalDate getPreviousBusinessDate(LocalDate date) {
        LocalDate result = date;
        while (isHoliday(result)) {
            result = result.minusDays(1);
        }
        return result;
    }


    private static boolean isHoliday(LocalDate localDate) {
        if (localDate.getDayOfWeek() == DayOfWeek.SATURDAY || localDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return true;
        } else {
            String dateStr = localDate.format(DateTimeFormatter.ofPattern("MM-dd"));
            return holidays.contains(dateStr);
        }
    }


    public static int getWorkingDates(LocalDate fromDate, LocalDate toDate) {
        int result = 0;
        while (fromDate.isBefore(toDate) || fromDate.equals(toDate)) {
            if (!isHoliday(toDate)) {
                result++;
            }
            toDate = toDate.minusDays(1);
        }
        return result;
    }

}
