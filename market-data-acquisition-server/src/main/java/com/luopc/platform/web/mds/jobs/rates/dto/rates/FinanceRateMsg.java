package com.luopc.platform.web.mds.jobs.rates.dto.rates;

import com.luopc.platform.web.mds.jobs.common.response.ResponseMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Robin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinanceRateMsg implements ResponseMessage {


    private int success;
    private FinanceRate result;

    @Override
    public boolean isSuccess() {
        return success == 1;
    }

}
