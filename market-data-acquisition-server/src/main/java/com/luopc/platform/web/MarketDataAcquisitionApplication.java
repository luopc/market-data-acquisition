package com.luopc.platform.web;

import com.luopc.platform.web.mds.config.EconomicsApiConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties(EconomicsApiConfig.class)
@EnableScheduling
public class MarketDataAcquisitionApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketDataAcquisitionApplication.class, args);
    }
}
