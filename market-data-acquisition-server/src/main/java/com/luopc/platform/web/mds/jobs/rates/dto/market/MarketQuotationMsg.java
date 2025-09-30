package com.luopc.platform.web.mds.jobs.rates.dto.market;

import com.luopc.platform.web.mds.jobs.common.response.ResponseMsg;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Robin
 */
@Data
@NoArgsConstructor
public class MarketQuotationMsg implements ResponseMsg {

    private boolean success;
    private String errorCode;
    private String errorInfo;
    private java.util.List<MarketDataDTO> data;

    @Override
    public boolean isSuccess() {
        return success;
    }
}
