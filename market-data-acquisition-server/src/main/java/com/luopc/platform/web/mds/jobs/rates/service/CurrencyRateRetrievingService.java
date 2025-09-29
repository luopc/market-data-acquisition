package com.luopc.platform.web.mds.jobs.rates.service;

import com.luopc.platform.market.api.*;
import com.luopc.platform.web.mds.config.EconomicsApiConfig;
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
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author by Robin
 * @className CurrencyRateRetrievingService
 * @description 获取各个国家的货币汇率
 * @date 2024/1/6 0006 20:22
 */
@Slf4j
@Setter
@Service
@NoArgsConstructor
public class CurrencyRateRetrievingService {

    @Resource
    private EconomicsApiConfig economicsApiConfig;

    public List<SpotRate> getCurrencyRates() {
        String url = economicsApiConfig.getCurrencyRateUrl();
        return getCurrencyRates(url);
    }

    /**
     * @param url = <a href="https://zh.tradingeconomics.com/currencies">国际汇率</a>
     * @return 国际汇率
     */
    private List<SpotRate> getCurrencyRates(String url) {
        List<SpotRate> spotRateList = new ArrayList<>();
        try {
            //获取连接
            Connection con = Jsoup.connect(url);
            //选择发送方式,获取整个网页信息，存在documnet类
            Document document = con.get();
            //通过class属性 ，获取子类元素
            Elements currencyTables = document.body().getElementsByClass("table-heatmap");

            currencyTables.forEach(pTable -> {
                Elements trs = pTable.getElementsByTag("tbody").get(0).children();
                //遍历<tr>标签
                trs.forEach(tr -> {
                    if (!tr.children().isEmpty()) {
                        String ccyPairSymbol = tr.getElementsByTag("td").get(1).text();
                        String price = tr.getElementsByTag("td").get(2).text();
                        if (ccyPairSymbol.length() == 6) {
                            CcyPair ccyPair = CcyPair.getInstance(ccyPairSymbol);
                            spotRateList.add(new SpotRate(ccyPair, new BigDecimal(price), LocalDateTime.now()));
                        }
                    }
                });
            });

        } catch (IOException e) {
            log.error("Unable to retrieve data from API, url = {}", url, e);
        }
        return spotRateList;
    }

    public String retrieveExchangeRates() {
        String url = economicsApiConfig.getExchangeRatesUrl();

        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6)");
        httpGet.addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);

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
}
