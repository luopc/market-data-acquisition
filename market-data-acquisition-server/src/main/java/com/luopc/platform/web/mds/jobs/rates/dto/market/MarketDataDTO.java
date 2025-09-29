package com.luopc.platform.web.mds.jobs.rates.dto.market;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MarketDataDTO {

    private String exchangeName;
    private List<MarketQuoteDTO> codeList;

}
