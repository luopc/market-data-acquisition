package com.luopc.platform.web.mds.rates.domain;

import com.alibaba.fastjson2.JSON;
import com.luopc.platform.web.mds.jobs.bank.dto.IBankData;
import com.luopc.platform.web.mds.jobs.rates.dto.market.MarketQuotationMsg;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class MarketQuotationMsgTest {

    @Test
    public void convert() {
        String sourceFile = "message/marketQuotation.json";
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(sourceFile)) {
            MarketQuotationMsg message = JSON.parseObject(inputStream, MarketQuotationMsg.class);
            Assertions.assertTrue(message != null && message.isSuccess());
        } catch (IOException var4) {
            log.error("Unable to retrieve data from Local, sourceFile = {}", sourceFile, var4);
        }
    }

    @Test
    public void convertJpy(){
        IBankData iBankData = new IBankData();
        iBankData.setUnit(1);
        iBankData.setName("JPY 日元");
        iBankData.setCashBuy("4.9182");
        iBankData.setCashSell("4.9627");

        log.info("{}", iBankData.getCashBuy());
        log.info("{}", iBankData.getCashSell());
    }

}
