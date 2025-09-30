package com.luopc.platform.web.mds.restful.controller;

import com.luopc.platform.market.api.Currency;
import com.luopc.platform.web.mds.jobs.bank.api.IBankDataApiCallService;
import com.luopc.platform.web.mds.jobs.bank.service.BankQuoteRetrievingService;
import com.luopc.platform.web.mds.rates.domain.dto.BankQuotation;
import com.luopc.platform.web.result.ResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/ibank", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "IBankDataApiController", description = "从 https://www.iban.hk 中获取到最新的汇率信息")
public class IBankDataApiController {

    @Resource
    private IBankDataApiCallService iBankDataApiCallService;
    @Resource
    private BankQuoteRetrievingService bankQuoteRetrievingService;

    @GetMapping(value = "/query/{ccy}")
    @ResponseBody
    @Operation(summary = "获取Currency的汇率数据", description = "Author @Robin")
    public ResponseMessage<Object> getQuoteFromApi(@PathVariable String ccy) {
        List<BankQuotation> bankQuotationList = bankQuoteRetrievingService.getQuoteFromApi(Currency.getInstance(ccy));
        return ResponseMessage.success(bankQuotationList);
    }


    @GetMapping("/getAll")
    @Operation(summary = "获取IBank的汇率数据", description = "不需要登录后访问")
    public ResponseMessage<Object> getAllBankQuotation() {
        List<BankQuotation> bankQuotationList = iBankDataApiCallService.getExchangeQuotationFromAPI();
        return ResponseMessage.success(bankQuotationList);
    }

}
