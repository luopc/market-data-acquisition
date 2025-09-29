package com.luopc.platform.web.mds.jobs.rates.api;

import com.alibaba.fastjson2.JSON;
import com.luopc.platform.market.api.*;
import com.luopc.platform.market.tools.RateCalculator;
import com.luopc.platform.web.mds.jobs.common.NowApiCallService;
import com.luopc.platform.web.mds.jobs.rates.dto.rates.FinanceRate;
import com.luopc.platform.web.mds.jobs.rates.dto.rates.FinanceRateMsg;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author Robin
 */
@Slf4j
@Setter
@Service
@NoArgsConstructor
public class FinanceRatesApiCallService extends NowApiCallService {


    public SpotRate getSpotRateFromApi(Currency priCcy, Currency cntCcy) {
        log.info("Going to get spotRate from Api, {}/{}", priCcy, cntCcy);
        if (priCcy.equals(cntCcy)) {
            return new SpotRate(priCcy.getCcyCode(), cntCcy.getCcyCode(), 1D);
        } else {
            FinanceRateMsg pairRateMessage = retrieveFinanceRate(priCcy.getCcyCode(), cntCcy.getCcyCode());
            if (pairRateMessage.isSuccess()) {
                FinanceRate pairFinanceRate = pairRateMessage.getResult();
                double finalRate = RateCalculator.calculateRateInDouble(priCcy.getCcyCode(), pairFinanceRate.getSpotRate());
                LocalDateTime updateDate = LocalDateTime.ofInstant(pairFinanceRate.getUpdate().toInstant(), ZoneId.systemDefault());
                return new SpotRate(priCcy.getCcyCode(), cntCcy.getCcyCode(), finalRate, updateDate);
            } else {
                if (!priCcy.isUsdFlag() && !cntCcy.isUsdFlag()) {
                    FinanceRate priFinanceRate = null;
                    FinanceRateMsg priRateMessage = retrieveFinanceUSDRate(priCcy.getCcyCode());
                    if (priRateMessage.isSuccess()) {
                        priFinanceRate = priRateMessage.getResult();
                    }
                    FinanceRate cntFinanceRate = null;
                    FinanceRateMsg cntRateMessage = retrieveFinanceUSDRate(cntCcy.getCcyCode());
                    if (cntRateMessage.isSuccess()) {
                        cntFinanceRate = cntRateMessage.getResult();
                    }

                    if (priFinanceRate != null && cntFinanceRate != null) {
                        double finalRate = RateCalculator.calculateRateInDouble(priCcy.getCcyCode(), priFinanceRate.getSpotRate(), cntCcy.getCcyCode(), cntFinanceRate.getSpotRate());
                        LocalDateTime updateDate = LocalDateTime.ofInstant(cntFinanceRate.getUpdate().toInstant(), ZoneId.systemDefault());
                        return new SpotRate(priCcy.getCcyCode(), cntCcy.getCcyCode(), finalRate, updateDate);
                    }
                } else {
                    String queryCcy = priCcy.isUsdFlag() ? cntCcy.getCcyCode() : priCcy.getCcyCode();
                    FinanceRateMsg rateMessage = retrieveFinanceUSDRate(queryCcy);
                    if (rateMessage.isSuccess()) {
                        FinanceRate result = rateMessage.getResult();
                        double finalRate = RateCalculator.calculateRateInDouble(priCcy.getCcyCode(), result.getSpotRate());
                        LocalDateTime updateDate = LocalDateTime.ofInstant(result.getUpdate().toInstant(), ZoneId.systemDefault());
                        return new SpotRate(priCcy.getCcyCode(), cntCcy.getCcyCode(), finalRate, updateDate);
                    }
                }
            }
        }
        return null;
    }

    public FinanceRateMsg retrieveFinanceCNYRate(String ccy) {
        return retrieveFinanceRate("CNY", ccy);
    }

    public FinanceRateMsg retrieveFinanceUSDRate(String ccy) {
        return retrieveFinanceRate("USD", ccy);
    }

    public FinanceRateMsg retrieveFinanceRate(String ccy1, String ccy2) {
        String url = getNowApiFinanceRate(ccy1, ccy2);
        log.info("CcyPairRate Request url: {}", url);
        if (isNotDev()) {
            String result = getData(url);
            return JSON.parseObject(result, FinanceRateMsg.class);
        } else {
            return new FinanceRateMsg();
        }
    }

    /**
     * http://api.k780.com/?app=finance.rate&format=json&appkey=69070&sign=2e3bee938fd4a7104181938a9d5d3d0f&scur=GBP&tcur=USD
     *
     * @param ccy1
     * @param ccy2
     * @return {
     * "success": "1",
     * "result": {
     * "status": "ALREADY",
     * "scur": "GBP",
     * "tcur": "USD",
     * "ratenm": "英镑/美元",
     * "rate": "1.26770026",
     * "update": "2023-12-16 06:29:44"
     * }
     * }
     */
    private String getNowApiFinanceRate(String ccy1, String ccy2) {
        return getNowApiURIBuilder()
                .queryParam("app", economicsApiConfig.getNowapiFinanceRate())
                .queryParam("scur", ccy1)
                .queryParam("tcur", ccy2)
                .toUriString();
    }

}
