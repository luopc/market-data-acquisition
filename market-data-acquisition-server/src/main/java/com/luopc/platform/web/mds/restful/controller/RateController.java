package com.luopc.platform.web.mds.restful.controller;

import com.luopc.platform.web.mds.restful.domain.from.RateQueryForm;
import com.luopc.platform.web.mds.restful.domain.vo.MarketQuoteVO;
import com.luopc.platform.web.mds.restful.domain.vo.RateVO;
import com.luopc.platform.web.mds.restful.service.RateService;
import com.luopc.platform.web.result.ResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Robin
 */
@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "RateController", description = "汇率管理")
@RequestMapping(value = "/api/rate", produces = MediaType.APPLICATION_JSON_VALUE)
public class RateController {

    @Resource
    private RateService rateService;

    @PostMapping(value = "spot/query")
    @ResponseBody
    @Operation(summary = "汇率-Spot汇率查询", description = "Author @Robin")
    public ResponseMessage<RateVO> querySpotRate(@Valid @RequestBody RateQueryForm queryForm) {
        log.info("Query SpotRate param= {}", queryForm);
        return ResponseMessage.success(rateService.getSpotRateByCcy(queryForm));
    }

    @GetMapping(value = "spot/all")
    @ResponseBody
    @Operation(summary = "汇率-Spot汇率查询全部", description = "Author @Robin")
    public ResponseMessage<String> querySpotAll() {
        return ResponseMessage.success(rateService.getAllSpotRates());
    }

    @PostMapping(value = "forward/query")
    @ResponseBody
    @Operation(summary = "汇率-Forward汇率查询", description = "Author @Robin")
    public ResponseMessage<String> queryForwardRate(@Valid @RequestBody RateQueryForm queryForm) {
        log.info("Query ForwardRate param= {}", queryForm);
        return ResponseMessage.success(rateService.getForwardPoint(queryForm));
    }

    @GetMapping(value = "forward/all")
    @ResponseBody
    @Operation(summary = "汇率-Forward汇率查询全部", description = "Author @Robin")
    public ResponseMessage<String> queryForwardAll() {
        return ResponseMessage.success(rateService.getAllForwardPoints());
    }

    @PostMapping(value = "/market/query")
    @ResponseBody
    @Operation(summary = "汇率-市场价格查询", description = "Author @Robin")
    public ResponseMessage<MarketQuoteVO> queryMarketRate(@Valid @RequestBody RateQueryForm queryForm) {
        log.info("Query MarketRate param= {}", queryForm);
        return ResponseMessage.success(rateService.getMarketRate(queryForm));
    }

    @GetMapping(value = "/market/all")
    @ResponseBody
    @Operation(summary = "汇率-市场价格查询全部", description = "Author @Robin")
    public ResponseMessage<List<MarketQuoteVO>> queryMarketRateAll() {
        return ResponseMessage.success(rateService.getAllMarketRates());
    }

}
