package com.luopc.platform.web.mds.jobs.common;

import com.alibaba.fastjson2.JSON;
import com.luopc.platform.common.core.env.SystemEnvironment;
import com.luopc.platform.web.config.SystemEnvironmentConfig;
import com.luopc.platform.web.mds.config.EconomicsApiConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author by Robin
 * @className NowApiCallService
 * @date 2024/1/6 0006 10:32
 */
@Slf4j
public class NowApiCallService {

    @Resource
    protected EconomicsApiConfig economicsApiConfig;

    @Resource
    protected SystemEnvironmentConfig systemEnvironmentConfig;

    protected boolean isNotDev() {
        SystemEnvironment systemEnvironment = systemEnvironmentConfig.initEnvironment();
        return !systemEnvironment.isDev();
    }

    public String postData(String uri, Map<String, Object> params) {
        HttpPost httpPost = new HttpPost(uri);
        String resultMessage = "";
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            httpPost.setEntity(new StringEntity(JSON.toJSONString(params), StandardCharsets.UTF_8));
            httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6)");
            httpPost.addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            CloseableHttpResponse response = httpclient.execute(httpPost);
            if (HttpStatus.OK_200 == response.getCode()) {
                resultMessage = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                log.info("Retrieving data from url {}", uri);
            }
        } catch (IOException e) {
            log.error("Unable to retrieve data from API server, url = {}", uri);
        } catch (ParseException e) {
            log.error("Unable to parse data from API server, url = {}", uri);
        }
        return resultMessage;
    }

    public String getData(String uri) {
        HttpGet httpGet = new HttpGet(uri);
        String resultMessage = "";
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            CloseableHttpResponse response = httpclient.execute(httpGet);
            if (HttpStatus.OK_200 == response.getCode()) {
                resultMessage = EntityUtils.toString(response.getEntity(), "utf-8");
                log.info("Retrieving data from url {}", uri);
            }
        } catch (IOException e) {
            log.error("Unable to retrieve data from NowApi, url = {}", uri);
        }  catch (ParseException e) {
            log.error("Unable to parse data from API server, url = {}", uri);
        }
        return resultMessage;
    }


    protected UriComponentsBuilder getNowApiURIBuilder() {
        return UriComponentsBuilder.fromUriString(economicsApiConfig.getNowapiUrl())
                .queryParam("format", "json")
                .queryParam("appkey", economicsApiConfig.getNowapiAppKey())
                .queryParam("sign", economicsApiConfig.getNowapiSign());
    }
}
