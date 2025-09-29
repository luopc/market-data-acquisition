package com.luopc.platform.web.mds.rates.domain.dto;

import com.luopc.platform.market.api.CcyPair;
import com.luopc.platform.market.api.Tenor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author by Robin
 * @className Quotate
 * @description TODO
 * @date 2024/1/4 0004 22:56
 */
@Data
@ToString
@AllArgsConstructor
public class QuoteByTenor {

    private String bankCode;
    private Tenor tenor;
    private CcyPair ccyPair;
    private BigDecimal buy;
    private BigDecimal sell;
    private BigDecimal middle;

}
