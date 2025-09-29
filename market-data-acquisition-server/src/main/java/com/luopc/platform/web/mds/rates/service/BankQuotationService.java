package com.luopc.platform.web.mds.rates.service;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.luopc.platform.web.mds.convertors.MapStructConvertor;
import com.luopc.platform.web.mds.handler.event.BankQuotationEvent;
import com.luopc.platform.web.mds.rates.domain.dto.BankQuotation;
import com.luopc.platform.web.mds.rates.domain.entity.BankQuotationDO;
import com.luopc.platform.web.mds.rates.mappers.BankQuotationMapper;
import jakarta.annotation.Resource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author Robin
 */
@Slf4j
@Setter
@Service
@Transactional(rollbackFor = Exception.class)
public class BankQuotationService {

    @Resource
    private MapStructConvertor mapStructConvertor;
    @Resource
    private BankQuotationMapper bankQuotationMapper;

    @Async
    @EventListener(value = BankQuotationEvent.class)
    public void handleBankQuotationEvent(BankQuotationEvent bankQuotationEvent) {
        log.info("Receive BankQuotationEvent message, {}", bankQuotationEvent.getChangeList().size());
        if (CollectionUtils.isNotEmpty(bankQuotationEvent.getChangeList())) {
            List<BankQuotation> bankQuotationList = bankQuotationEvent.getChangeList();
            bankQuotationList.forEach(this::saveOrUpdate);
        }
    }

    public BankQuotationDO saveOrUpdate(BankQuotation bankQuotation) {
        BankQuotationDO bankQuotationDO = new LambdaQueryChainWrapper<>(bankQuotationMapper)
                .eq(BankQuotationDO::getBankCode, bankQuotation.getBankCode())
                .eq(BankQuotationDO::getBaseCcy, bankQuotation.getBaseCcy())
                .eq(BankQuotationDO::getQuoteCcy, bankQuotation.getQuoteCcy())
                .one();

        if (Objects.isNull(bankQuotationDO)) {
            bankQuotationDO = mapStructConvertor.bankQuotationToDO(bankQuotation);
            int status = bankQuotationMapper.insert(bankQuotationDO);
            return bankQuotationDO;
        } else {
            BankQuotationDO newDO = mapStructConvertor.bankQuotationToDO(bankQuotation);
            newDO.setId(bankQuotationDO.getId());
            int status = bankQuotationMapper.updateById(newDO);
        }
        return bankQuotationMapper.selectById(bankQuotationDO.getId());
    }

}
