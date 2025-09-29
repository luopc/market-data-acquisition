package com.luopc.platform.web.mds.rates.domain;

import cn.hutool.core.io.file.FileReader;
import com.alibaba.fastjson2.JSON;
import com.luopc.platform.web.mds.jobs.rates.dto.rates.FinanceRateMsg;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class RateMessageConvertTest {

    @Test
    public void convert() {
        FileReader reader = new FileReader("message/finance.rate.json");
        FinanceRateMsg message = JSON.parseObject(reader.getInputStream(), FinanceRateMsg.class);
        log.info("RateMessage from JSON: {}", message);
        Double rate = message.getResult().getRate();
        log.info("ccy pair rate = " + rate.toString());
    }

    @Test
    public void convert2() {
        FileReader reader = new FileReader("message/finance.rate2.json");
        FinanceRateMsg message = JSON.parseObject(reader.getInputStream(), FinanceRateMsg.class);
        log.info("RateMessage from JSON: {}", message);
        Double rate = message.getResult().getRate();
        log.info("ccy pair rate = " + rate.toString());
    }

}
