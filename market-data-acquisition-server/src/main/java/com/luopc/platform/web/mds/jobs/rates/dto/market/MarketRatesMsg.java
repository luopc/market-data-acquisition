package com.luopc.platform.web.mds.jobs.rates.dto.market;

import com.luopc.platform.web.mds.jobs.common.response.ResponseMessage;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Objects;

/**
 * @author Robin
 */
@Data
@NoArgsConstructor
public class MarketRatesMsg implements ResponseMessage {

    private Long timestamp;
    private Map<String, Double> rates;

    @Override
    public boolean isSuccess() {
        return Objects.nonNull(rates);
    }
}
