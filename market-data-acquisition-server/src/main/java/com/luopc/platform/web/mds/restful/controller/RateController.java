package com.luopc.platform.web.mds.restful.controller;

import com.luopc.platform.web.mds.restful.domain.from.RateQueryForm;
import com.luopc.platform.web.mds.restful.domain.vo.MarketQuoteVO;
import com.luopc.platform.web.mds.restful.domain.vo.RateVO;
import com.luopc.platform.web.mds.restful.service.RateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * @author Robin
 */
@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "RateController", description = "汇率管理")
public class RateController {

    @Resource
    private RateService rateService;

    @GetMapping(value = "rate/interest/query/{ccy}")
    @ResponseBody
    @Operation(summary = "汇率-Interest利率查询", description = "Author @Robin")
    public ResponseEntity<Double> queryInterest(@PathVariable String ccy) {
        log.info("Query InterestRate param= {}", ccy);
        return rateService.getInterestRateByCcy(ccy);
    }

    @GetMapping(value = "rate/interest/queryAll")
    @ResponseBody
    @Operation(summary = "汇率-Interest利率查询", description = "Author @Robin")
    public ResponseEntity<String> queryInterestAll() {
        return rateService.getAllInterestRates();
    }

    @PostMapping(value = "rate/spot/query")
    @ResponseBody
    @Operation(summary = "汇率-Spot汇率查询", description = "Author @Robin")
    public ResponseEntity<RateVO> querySpotRate(@Valid @RequestBody RateQueryForm queryForm) {
        log.info("Query SpotRate param= {}", queryForm);
        return rateService.getSpotRateByCcy(queryForm);
    }

    @GetMapping(value = "rate/spot/all")
    @ResponseBody
    @Operation(summary = "汇率-Spot汇率查询全部", description = "Author @Robin")
    public ResponseEntity<String> querySpotAll() {
        return rateService.getAllSpotRates();
    }

    @PostMapping(value = "rate/forward/query")
    @ResponseBody
    @Operation(summary = "汇率-Forward汇率查询", description = "Author @Robin")
    public ResponseEntity<String> queryForwardRate(@Valid @RequestBody RateQueryForm queryForm) {
        log.info("Query ForwardRate param= {}", queryForm);
        return rateService.getForwardPoint(queryForm);
    }

    @GetMapping(value = "rate/forward/all")
    @ResponseBody
    @Operation(summary = "汇率-Forward汇率查询全部", description = "Author @Robin")
    public ResponseEntity<String> queryForwardAll() {
        return rateService.getAllForwardPoints();
    }

    @PostMapping(value = "rate/market/query")
    @ResponseBody
    @Operation(summary = "汇率-市场价格查询", description = "Author @Robin")
    public ResponseEntity<MarketQuoteVO> queryMarketRate(@Valid @RequestBody RateQueryForm queryForm) {
        log.info("Query MarketRate param= {}", queryForm);
        return rateService.getMarketRate(queryForm);
    }

    @GetMapping(value = "rate/market/all")
    @ResponseBody
    @Operation(summary = "汇率-市场价格查询全部", description = "Author @Robin")
    public ResponseEntity<List<MarketQuoteVO>> queryMarketRateAll() {
        return rateService.getAllMarketRates();
    }

}
