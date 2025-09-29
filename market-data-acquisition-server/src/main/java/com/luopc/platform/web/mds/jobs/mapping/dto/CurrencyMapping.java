package com.luopc.platform.web.mds.jobs.mapping.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Robin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyMapping {

    private String ccy;
    private String ccyNum;
    private String currencyName;
    private String countryName;
    private String sourcePlatform;

    public CurrencyMapping(String ccy, String ccyNum, String currencyName, String countryName) {
        this.ccy = ccy;
        this.ccyNum = ccyNum;
        this.currencyName = currencyName;
        this.countryName = countryName;
    }
}
