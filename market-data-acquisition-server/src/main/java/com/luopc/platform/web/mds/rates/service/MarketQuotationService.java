package com.luopc.platform.web.mds.rates.service;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.luopc.platform.market.api.MarketPrices;
import com.luopc.platform.web.mds.handler.event.MarketPricesEvent;
import com.luopc.platform.web.mds.rates.domain.entity.MarketQuotationDO;
import com.luopc.platform.web.mds.rates.mappers.MarketQuotationMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author Robin
 */
@Slf4j
@Setter
@Service
@Transactional(rollbackFor = Exception.class)
public class MarketQuotationService {

    @Resource
    private MarketQuotationMapper marketQuotationMapper;

    @Async
    @EventListener(value = MarketPricesEvent.class)
    public void handleMarketQuotationEvent(MarketPricesEvent marketPricesEvent) {
        log.info("Receive MarketQuotationEvent message, {}", marketPricesEvent.getChangeList().size());
        if (CollectionUtils.isNotEmpty(marketPricesEvent.getChangeList())) {
            List<MarketPrices> bankQuotationList = marketPricesEvent.getChangeList();
            bankQuotationList.forEach(this::saveOrUpdate);
        }
    }

    public MarketQuotationDO saveOrUpdate(MarketPrices marketPrices) {
        MarketQuotationDO marketQuotationDO = new LambdaQueryChainWrapper<>(marketQuotationMapper)
                .eq(MarketQuotationDO::getBaseCcy, marketPrices.getCcyPair().getCcy1().getCcyCode())
                .eq(MarketQuotationDO::getQuoteCcy, marketPrices.getCcyPair().getCcy2().getCcyCode())
                .one();


        StringJoiner lastPrices = new StringJoiner(",");
        marketPrices.getLastPrices().forEach(prices -> {
            DecimalFormat df = new DecimalFormat("#.000000");
            lastPrices.add(df.format(prices));
        });

        if (Objects.isNull(marketQuotationDO)) {
            marketQuotationDO = new MarketQuotationDO();
            marketQuotationDO.setBaseCcy(marketPrices.getCcyPair().getCcy1().getCcyCode());
            marketQuotationDO.setQuoteCcy(marketPrices.getCcyPair().getCcy2().getCcyCode());
            marketQuotationDO.setBuy(marketPrices.getBuy());
            marketQuotationDO.setSell(marketPrices.getSell());
            marketQuotationDO.setLastPrices(lastPrices.toString());
            int status = marketQuotationMapper.insert(marketQuotationDO);
            return marketQuotationDO;
        } else {
            MarketQuotationDO newDO = new MarketQuotationDO();
            newDO.setId(marketQuotationDO.getId());
            newDO.setBaseCcy(marketPrices.getCcyPair().getCcy1().getCcyCode());
            newDO.setQuoteCcy(marketPrices.getCcyPair().getCcy2().getCcyCode());
            newDO.setBuy(marketPrices.getBuy());
            newDO.setSell(marketPrices.getSell());
            newDO.setLastPrices(lastPrices.toString());
            int status = marketQuotationMapper.updateById(newDO);
        }
        return marketQuotationMapper.selectById(marketQuotationDO.getId());
    }
}
