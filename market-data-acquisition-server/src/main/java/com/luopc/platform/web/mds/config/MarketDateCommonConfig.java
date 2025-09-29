package com.luopc.platform.web.mds.config;

import com.luopc.platform.web.mds.common.TradeMainCcyPairHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author by Robin
 * @className MarketDateCommonConfig
 * @date 2024/1/22 0022 20:50
 */
@Configuration
public class MarketDateCommonConfig {
    @Bean
    public TradeMainCcyPairHelper tradeMainCcyPairHelper() {
        //单例模式练习
        //Spring 默认创建的bean都是单例的，通过注解@Scope("prototype") 可以修改为非单例
        return TradeMainCcyPairHelper.getInstance();
    }

}
