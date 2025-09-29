package com.luopc.platform.web.mds.rates.service;

import com.luopc.platform.web.mds.handler.event.InterestRateEvent;
import com.luopc.platform.web.mds.rates.domain.entity.CcyInterestDO;
import com.luopc.platform.web.mds.rates.mappers.CcyInterestMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author Robin
 */
@Slf4j
@Setter
@Service
@Transactional(rollbackFor = Exception.class)
public class CcyInterestService {

    @Resource
    private CcyInterestMapper ccyInterestMapper;

    public CcyInterestDO saveOrUpdate(CcyInterestDO interestRate) {
        CcyInterestDO ccyInterestDO = ccyInterestMapper.selectById(interestRate.getCcy());
        if (Objects.isNull(ccyInterestDO)) {
            ccyInterestMapper.insert(interestRate);
            return interestRate;
        } else {
            ccyInterestMapper.updateById(interestRate);
        }
        return ccyInterestMapper.selectById(interestRate.getCcy());
    }


    @Async
    @EventListener(value = InterestRateEvent.class)
    public void handleSpotRateEvent(InterestRateEvent interestRateEvent) {
        log.info("Receive interestRateEvent message, {}", interestRateEvent.getChangeList().size());
        if (CollectionUtils.isNotEmpty(interestRateEvent.getChangeList())) {
            List<CcyInterestDO> interestRateList = interestRateEvent.getChangeList();
            interestRateList.forEach(this::saveOrUpdate);
        }
    }

}
