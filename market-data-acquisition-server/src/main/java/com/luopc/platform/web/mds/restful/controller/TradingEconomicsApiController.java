package com.luopc.platform.web.mds.restful.controller;

import com.luopc.platform.web.mds.jobs.rates.service.InterestRatesRetrievingService;
import com.luopc.platform.web.mds.rates.domain.entity.CcyInterestDO;
import com.luopc.platform.web.result.ResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/trading", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "TradingEconomicsApiController", description = "从 https://zh.tradingeconomics.com 中获取到最新的汇率信息")
public class TradingEconomicsApiController {

    @Resource
    private InterestRatesRetrievingService interestRatesRetrievingService;

    @GetMapping("/getAll")
    @Operation(summary = "获取trading economics的利率数据", description = "不需要登录后访问")
    public ResponseMessage<Object> getAllBankQuotation() {
        List<CcyInterestDO> ccyInterestList = interestRatesRetrievingService.getInterestRates();
        return ResponseMessage.success(ccyInterestList);
    }
}
