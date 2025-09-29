package com.luopc.platform.web.mds.rates.domain;

import cn.hutool.core.io.file.FileReader;
import com.alibaba.fastjson2.JSON;
import com.luopc.platform.web.mds.jobs.bank.dto.QuoteMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class QuoteMessageConvertTest {


    @Test
    public void convert() {
        FileReader reader = new FileReader("message/finance.rate_cnyquot.json");
        QuoteMessage message = JSON.parseObject(reader.getInputStream(), QuoteMessage.class);
        message.setQuoteCcy("CNY");
        Assertions.assertTrue(message.isSuccess());
    }

}
