package com.luopc.platform.web.mds.produce;

import com.luopc.platform.cloud.amq.quques.MarketDataEventTagConst;

import com.luopc.platform.market.api.*;
import com.luopc.platform.market.tools.RateFormatter;
import com.luopc.platform.web.mds.common.TradeMainCcyPairHelper;
import com.luopc.platform.web.mds.rates.domain.dto.BankQuotation;
import com.luopc.platform.web.mds.restful.manager.MarketDataRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * @author Robin
 */
@Slf4j
@Component
public class MarketDataProducer {

    @Resource
    RabbitTemplate rabbitTemplate;
    @Resource
    MarketDataRepository marketDataRepository;
    @Resource
    private TradeMainCcyPairHelper tradeMainCcyPairHelper;

    //@Scheduled(cron = "0/15 * * ? * MON-SUN")
    public void publish() {
        CcyPair ccyPair = tradeMainCcyPairHelper.getCcyPair();
        log.info("Going to publish rate and quote for ccyPair[{}]", ccyPair);
        if(Objects.nonNull(ccyPair)) {
            publishSpotRate(marketDataRepository.getSpotRate(ccyPair));
            publishBankQuote(marketDataRepository.getBankQuote(ccyPair));
            publishMarketPrices(marketDataRepository.getMarketRate(ccyPair));
            publishForwardRate(marketDataRepository.getForwardRates(ccyPair));
        }

    }

    @Scheduled(initialDelay = 30 * 1000, fixedDelay = 60 * 60 * 1000)
    public void updateMainCcyPair() {
        tradeMainCcyPairHelper.addCcyPairs(marketDataRepository.getMainCcyPairs());
    }

    private void publishSpotRate(SpotRate spotRate) {
        if (Objects.nonNull(spotRate)) {
            String message = RateFormatter.formatSpotRate(spotRate);
            CcyPair ccyPair = spotRate.getCcyPair();
            String topic = MarketDataEventTagConst.Rates.SPOT_RATES.replace("#", ccyPair.getCcyPairStr().replace("/", "."));
            log.debug("Going to publish message topic {} : {}", topic, message);
            sendMsgViaMq(message, topic);
        }
    }


    private void publishBankQuote(List<BankQuotation> bankQuotationList) {
        if (CollectionUtils.isNotEmpty(bankQuotationList)) {
//            bankQuotationMap.forEach((quote) -> {
            BankQuotation quote = bankQuotationList.getFirst();
            String msg = RateFormatter.formatBankQuote(quote.getBankCode(), quote);
            String topic = MarketDataEventTagConst.Bank.BANK_QUOTE.replace("#", (quote.getBaseCcy() + "." + quote.getQuoteCcy()));
            log.debug("Going to publish message {} : {}", topic, msg);
            sendMsgViaMq(msg, topic);
//            });

        }
    }


    private void publishForwardRate(ForwardRate forwardRate) {
        if (Objects.nonNull(forwardRate)) {
            String msg = RateFormatter.formatForwardRate(forwardRate);
            CcyPair ccyPair = forwardRate.getCcyPair();
            String topic = MarketDataEventTagConst.Rates.FORWARD_RATES.replace("#", ccyPair.getCcyPairStr().replace("/", "."));
            log.debug("Going to publish message {} : {}", topic, msg);
            sendMsgViaMq(msg, topic);
        }
    }


    private void publishMarketPrices(MarketPrices marketPrices) {
        if (Objects.nonNull(marketPrices)) {
            String message = RateFormatter.formatMarketPrices(marketPrices);
            CcyPair ccyPair = marketPrices.getCcyPair();
            String topic = MarketDataEventTagConst.Market.MARKET_PRICES.replace("#", ccyPair.getCcyPairStr().replace("/", "."));
            log.debug("Going to publish message {} : {}", topic, message);
            sendMsgViaMq(message, topic);
        }
    }

    private void sendMsgViaMq(String msg, String topic) {
        rabbitTemplate.convertAndSend(MarketDataEventTagConst.MARKET_DATA_EXCHANGE, topic, msg.getBytes(StandardCharsets.UTF_8));
    }

}
