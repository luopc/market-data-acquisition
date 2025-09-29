package com.luopc.platform.web.mds.restful.controller;


import com.luopc.platform.web.mds.restful.domain.from.QuoteQueryForm;
import com.luopc.platform.web.mds.restful.domain.vo.QuoteVO;
import com.luopc.platform.web.mds.restful.service.QuoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Robin
 */
@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "QuoteController", description = "报价管理")
public class QuoteController {

    @Resource
    private QuoteService quoteService;

    @PostMapping(value = "quote/bank/query")
    @Operation(summary = "报价-银行报价查询", description = "Author @Robin")
    public ResponseEntity<List<QuoteVO>> queryQuote(@Valid @RequestBody QuoteQueryForm queryForm) {
        log.info("Receive request from frontend, {}", queryForm);
        return quoteService.queryBankQuote(queryForm);
    }

}
