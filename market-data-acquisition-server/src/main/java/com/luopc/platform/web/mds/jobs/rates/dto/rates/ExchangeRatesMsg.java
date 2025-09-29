package com.luopc.platform.web.mds.jobs.rates.dto.rates;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author by Robin
 * @className ExchangeRatesMsg
 * @description TODO
 * @date 2024/1/6 0006 19:47
 */

@Data
@NoArgsConstructor
public class ExchangeRatesMsg {

    private List<ExchangeRate> records;

}
