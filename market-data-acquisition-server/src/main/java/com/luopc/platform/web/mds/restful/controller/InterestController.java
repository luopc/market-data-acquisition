package com.luopc.platform.web.mds.restful.controller;

import com.luopc.platform.market.tools.CountryCurrencyUtil;
import com.luopc.platform.web.mds.restful.domain.vo.InterestVO;
import com.luopc.platform.web.mds.restful.service.RateService;
import com.luopc.platform.web.result.ResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "InterestController", description = "利率管理")
@RequestMapping(value = "/api/interest", produces = MediaType.APPLICATION_JSON_VALUE)
public class InterestController {

    @Resource
    private RateService rateService;

    @GetMapping(value = "/query/{ccy}")
    @ResponseBody
    @Operation(summary = "汇率-Interest利率查询", description = "Author @Robin")
    public ResponseMessage<Object> queryInterest(@PathVariable String ccy) {
        log.info("Query InterestRate param= {}", ccy);
        Optional<String> ccyName = CountryCurrencyUtil.getCountriesByCurrency(ccy).stream().findAny();
        return ResponseMessage.success(new InterestVO(ccy, ccyName.orElse(""), rateService.getInterestRateByCcy(ccy)));
    }

    @GetMapping(value = "/queryAll")
    @ResponseBody
    @Operation(summary = "汇率-Interest利率查询", description = "Author @Robin")
    public ResponseMessage<Object> queryInterestAll() {
        return ResponseMessage.success(rateService.getAllInterestRates());
    }
}
