package com.luopc.platform.web.mds.rates.service;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.luopc.platform.market.api.SpotRate;
import com.luopc.platform.web.mds.convertors.MapStructConvertor;
import com.luopc.platform.web.mds.convertors.MarketDataConvertor;
import com.luopc.platform.web.mds.handler.event.SpotRateEvent;
import com.luopc.platform.web.mds.rates.domain.entity.SpotRateDO;
import com.luopc.platform.web.mds.rates.mappers.SpotRateMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Robin
 */
@Slf4j
@Setter
@Service
@Transactional(rollbackFor = Exception.class)
public class SpotRateService {

    @Resource
    private MapStructConvertor mapStructConvertor;
    @Resource
    private SpotRateMapper spotRateMapper;

    @Async
    @EventListener(value = SpotRateEvent.class)
    public void handleSpotRateEvent(SpotRateEvent spotRateEvent) {
        log.info("Receive SpotRateEvent message, {}", spotRateEvent.getChangeList().size());
        if (CollectionUtils.isNotEmpty(spotRateEvent.getChangeList())) {
            List<SpotRate> spotRateList = spotRateEvent.getChangeList();
            spotRateList.forEach(this::saveOrUpdate);
        }
    }

    public SpotRateDO saveOrUpdate(SpotRate spotRate) {
        SpotRateDO spotRateDO = new LambdaQueryChainWrapper<>(spotRateMapper)
                .eq(SpotRateDO::getCcy1, spotRate.getCcyPair().getCcy1().getCcyCode())
                .eq(SpotRateDO::getCcy2, spotRate.getCcyPair().getCcy2().getCcyCode())
                .one();

        if (Objects.isNull(spotRateDO)) {
            spotRateDO = MarketDataConvertor.spotRateToDO(spotRate);
            spotRateDO.setEodFlag(false);
            spotRateDO.setSnapshotDate(new Date());
            int status = spotRateMapper.insert(spotRateDO);
            return spotRateDO;
        } else {
            SpotRateDO newDO = MarketDataConvertor.spotRateToDO(spotRate);
            newDO.setId(spotRateDO.getId());
            newDO.setUpdatedTime(new Date());
            int status = spotRateMapper.updateById(newDO);
        }
        return spotRateMapper.selectById(spotRateDO.getId());
    }



}
