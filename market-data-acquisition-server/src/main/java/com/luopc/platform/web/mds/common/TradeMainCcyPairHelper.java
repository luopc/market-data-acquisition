package com.luopc.platform.web.mds.common;

import cn.hutool.core.lang.WeightRandom;
import com.luopc.platform.market.api.CcyPair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author by Robin
 * @className TradeMainCcyPairHelper
 * @description 获取主要的交易货币对
 * @date 2024/1/22 0022 12:51
 */
@Slf4j
public class TradeMainCcyPairHelper {

    private static volatile TradeMainCcyPairHelper instance;
    private final Set<CcyPair> mainCcyPairs = new HashSet<>();
    private final WeightRandom<CcyPair> ccyPairsWeightRandom;

    private TradeMainCcyPairHelper() {
        ccyPairsWeightRandom = new WeightRandom<>();
    }

    public static TradeMainCcyPairHelper getInstance() {
        if (instance == null) {
            synchronized (TradeMainCcyPairHelper.class) {
                if (instance == null) {
                    instance = new TradeMainCcyPairHelper();
                }
            }
        }
        return instance;
    }

    public void addCcyPairs(Set<CcyPair> ccyPairs) {
        if (CollectionUtils.isNotEmpty(ccyPairs)) {
            Set<CcyPair> updateCcyPairs = new HashSet<>();
            ccyPairs.forEach(ccyPair -> {
                if (!mainCcyPairs.contains(ccyPair)) {
                    if (ccyPair.isClsCcyPair()) {
                        ccyPairsWeightRandom.add(ccyPair, 20);
                    } else if (ccyPair.isG20CcyPair()) {
                        ccyPairsWeightRandom.add(ccyPair, 10);
                    } else if (ccyPair.isNdfFlag()) {
                        ccyPairsWeightRandom.add(ccyPair, 5);
                    } else {
                        ccyPairsWeightRandom.add(ccyPair, 2);
                    }
                    updateCcyPairs.add(ccyPair);
                }
            });
            if (CollectionUtils.isNotEmpty(updateCcyPairs)) {
                mainCcyPairs.addAll(updateCcyPairs);
                log.info("update mainCcyPairs as: {}", mainCcyPairs.stream().map(CcyPair::toString).collect(Collectors.toList()));
            }
        }
    }

    public CcyPair getCcyPair() {
        return ccyPairsWeightRandom.next();
    }
}
