package com.luopc.platform.web.mds.jobs.rates.service;

import com.alibaba.fastjson2.JSON;
import com.luopc.platform.web.mds.config.EconomicsApiConfig;
import com.luopc.platform.web.mds.jobs.rates.dto.market.MarketRatesMsg;
import jakarta.annotation.Resource;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author by Robin
 * @className CurrencyRatesRetrieveService
 * @description 国家货币汇率数据
 * @date 2024/1/6 0006 10:44
 */
@Slf4j
@Setter
@Service
@NoArgsConstructor
public class MarketRatesRetrievingService {

    @Resource
    protected EconomicsApiConfig economicsApiConfig;

    public String retrieveMarketRates() {
        String url = economicsApiConfig.getMarketRatesUrl();

        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6)");
        httpGet.addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        httpGet.addHeader("Authorization", "Basic " + economicsApiConfig.getMarketRatesAuthorization());

        String resultMessage = "";
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            CloseableHttpResponse response = httpclient.execute(httpGet);
            if (HttpStatus.OK_200 == response.getCode()) {
                resultMessage = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                log.info("Retrieving data from url {}", url);
            }
        } catch (IOException e) {
            log.error("Unable to retrieve data from API server, url = {}", url);
        } catch (ParseException e) {
            log.error("Unable to parse data from API server, url = {}", url);
        }
        return resultMessage;
    }

    public MarketRatesMsg getLocalData() {
        String sourceFile = "static/marketRates-xe.json";
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(sourceFile)) {
            return JSON.parseObject(inputStream, MarketRatesMsg.class);
        } catch (IOException var4) {
            log.error("Unable to retrieve data from Local, sourceFile = {}", sourceFile, var4);
            return null;
        }
    }
}
