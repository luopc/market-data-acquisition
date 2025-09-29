package com.luopc.platform.web.mds.jobs.rates.dto.rates;

import com.luopc.platform.market.api.CcyPair;
import com.luopc.platform.market.api.SpotRate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

/**
 * @author Robin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinanceRate {

    private String status;
    private String scur;
    private String tcur;
    private String ratenm;
    private Double rate;
    private Date update;

    public SpotRate getSpotRate() {
        LocalDateTime date = LocalDateTime.now();
        if (Objects.nonNull(update)) {
            date = LocalDateTime.ofInstant(update.toInstant(), ZoneId.systemDefault());
        }
        return new SpotRate(CcyPair.getInstance(scur, tcur), BigDecimal.valueOf(rate), date);
    }
}
