package com.luopc.platform.web.mds.restful.service;

import com.alibaba.fastjson2.JSON;
import com.luopc.platform.common.core.exception.BusinessException;
import com.luopc.platform.market.api.*;
import com.luopc.platform.market.api.Currency;
import com.luopc.platform.market.tools.CountryCurrencyUtil;
import com.luopc.platform.web.mds.convertors.MarketDataConvertor;
import com.luopc.platform.web.mds.rates.domain.entity.CcyInterestDO;
import com.luopc.platform.web.mds.restful.domain.MDSErrorCode;
import com.luopc.platform.web.mds.restful.domain.from.RateQueryForm;
import com.luopc.platform.web.mds.restful.domain.vo.InterestVO;
import com.luopc.platform.web.mds.restful.domain.vo.MarketQuoteVO;
import com.luopc.platform.web.mds.restful.domain.vo.RateVO;
import com.luopc.platform.web.mds.restful.manager.MarketDataRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Robin
 */
@Slf4j
@Service
public class RateService {

    @Resource
    private MarketDataRepository marketDataRepository;


    public Double getInterestRateByCcy(String ccy) {
        return marketDataRepository.getInterestRate(Currency.getInstance(ccy)).getRate();
    }

    public List<InterestVO> getAllInterestRates() {
        List<CcyInterestDO> interestRateList = marketDataRepository.getAllInterestRates();
        List<InterestVO> interestVOList = interestRateList.stream().map(rate -> {
            Optional<String> ccyName = CountryCurrencyUtil.getCountriesByCurrency(rate.getCcy()).stream().findAny();
            return new InterestVO(rate.getCcy(), ccyName.orElse(""), rate.getRate());
        }).toList();
        log.info("Get all interest rates: {}", interestVOList.size());
        return interestVOList;
    }

    public RateVO getSpotRateByCcy(RateQueryForm queryForm) {
        Currency priCcy = Currency.getInstance(queryForm.getPriCcy());
        Currency cntCcy = Currency.getInstance(queryForm.getCntCcy());

        RateVO rateVO = new RateVO(priCcy.getCcyCode(), cntCcy.getCcyCode());
        SpotRate ccyPairRate = marketDataRepository.getSpotRate(priCcy, cntCcy);
        if (ccyPairRate != null) {
            rateVO.setRate(ccyPairRate.getRate().doubleValue());
            rateVO.setLastUpdateTime(ccyPairRate.getLastUpdateTime());
            return rateVO;
        } else {
            throw new BusinessException(MDSErrorCode.RATE_CANNOT_FOUND);
        }
    }

    public String getAllSpotRates() {
        Map<CcyPair, SpotRate> spotRateMap = marketDataRepository.getAllSpotRates();
        Map<String, Double> ccyPairDoubleMap = new HashMap<>();
        spotRateMap.forEach((ccyPair, spotRate) -> {
            ccyPairDoubleMap.put(ccyPair.getCcyPairStr(), spotRate.getRate().doubleValue());
        });
        return JSON.toJSONString(ccyPairDoubleMap);

    }

    public String getAllForwardPoints() {
        Map<CcyPair, ForwardRate> forwardRateMap = marketDataRepository.getAllForwardRates();
        List<String> results = new ArrayList<>(forwardRateMap.size());
        forwardRateMap.forEach((ccyPair, forwardRate) -> {
            StringJoiner tenors = new StringJoiner(",");
            forwardRate.getInterpolatorMap().values().forEach(interpolator -> {
                tenors.add(String.format("%s:%.4f", interpolator.getTenor().getCode(), interpolator.getPoint()));
            });
            results.add(String.format("%s, tenors=%s, fromFactor=%d", ccyPair.getCcyPairStr(), tenors, ForwardRate.FROM_FACTOR));

        });
        return JSON.toJSONString(results);
    }

    public String getForwardPoint(RateQueryForm queryForm) {
        ForwardRate forwardRate = marketDataRepository.getForwardRates(CcyPair.getInstance(queryForm.getPriCcy(), queryForm.getCntCcy()));
        if (Objects.nonNull(forwardRate)) {
            StringJoiner tenors = new StringJoiner(",");
            forwardRate.getInterpolatorMap().values().forEach(interpolator -> {
                tenors.add(String.format("%s:%.4f", interpolator.getTenor().getCode(), interpolator.getPoint()));
            });
            String result = String.format("%s, tenors=%s, fromFactor=%d", forwardRate.getCcyPair().getCcyPairStr(), tenors, ForwardRate.FROM_FACTOR);
            return JSON.toJSONString(result);
        } else {
            throw new BusinessException(MDSErrorCode.RATE_CANNOT_FOUND);
        }
    }

    public MarketQuoteVO getMarketRate(RateQueryForm queryForm) {
        MarketPrices marketPrices = marketDataRepository.getMarketRate(CcyPair.getInstance(queryForm.getPriCcy(), queryForm.getCntCcy()));
        if (Objects.nonNull(marketPrices)) {
            return MarketDataConvertor.getMarketQuoteVO(marketPrices);
        } else {
            throw new BusinessException(MDSErrorCode.RATE_CANNOT_FOUND);
        }
    }


    public List<MarketQuoteVO> getAllMarketRates() {
        Map<CcyPair, MarketPrices> marketQuoteMap = marketDataRepository.getAllMarketRates();
        List<MarketQuoteVO> quoteVOList = marketQuoteMap.values().stream().map(MarketDataConvertor::getMarketQuoteVO).collect(Collectors.toList());
        return quoteVOList;
    }

}
