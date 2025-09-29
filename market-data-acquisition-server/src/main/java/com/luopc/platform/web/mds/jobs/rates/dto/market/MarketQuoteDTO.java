package com.luopc.platform.web.mds.jobs.rates.dto.market;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class MarketQuoteDTO {

    private String buy;
    private String code;
    private String excode;
    private Integer id;
    private List<String> lastPrices;
    private String lastClose;
    private String margin;
    private String mp;
    private String name;
    private String sell;
    private String webName;

}
