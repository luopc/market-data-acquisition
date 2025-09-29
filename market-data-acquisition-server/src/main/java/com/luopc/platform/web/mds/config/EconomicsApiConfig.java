package com.luopc.platform.web.mds.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author Robin
 */
@Data
@Component
@ConfigurationProperties(value = "api")
@PropertySource("classpath:config/economics-api.properties")
@Configuration
//@NacosPropertySource(dataId = "economics-api.properties", autoRefreshed = true)
public class EconomicsApiConfig {

    //@NacosValue(value = "${api.nowapiUrl}", autoRefreshed = true)
    private String nowapiUrl;
    //@NacosValue(value = "${api.nowapiAppKey}", autoRefreshed = true)
    private String nowapiAppKey;
    //@NacosValue(value = "${api.nowapiSign}", autoRefreshed = true)
    private String nowapiSign;

    private String nowapiFinanceRate;
    private String nowapiFinanceRateCnyquot;
    private String nowapiFinanceRateUnionpayintl;

    private String bankCodes;
    private String bankQuoteUrl;
    private String currencyMappingUrl;
    private String ibanMappingUrl;

    private String interestRateUrl;
    private String interestContinents;
    private String currencyRateUrl;

    private String marketQuotationUrl;

    private String marketRatesUrl;
    private String marketRatesAuthorization;

    private String exchangeRatesUrl;
    //人民币历史汇率数据
    private String hisExchangeRatesUrl;

}
