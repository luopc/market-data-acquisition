package com.luopc.platform.web.mds.restful.controller;


import com.luopc.platform.web.mds.restful.domain.vo.QuoteVO;
import com.luopc.platform.web.mds.restful.service.QuoteService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 无法解决 Nacos 依赖，无法启动web服务器
 *
 * @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
 * @AutoConfigureMockMvc
 */
@Disabled
@Slf4j
@WebMvcTest(controllers = QuoteController.class)
public class QuoteControllerTest {

    @Resource
    private MockMvc mockMvc;
    @MockitoBean
    QuoteService quoteService;

    @Test
    void testQueryQuote() {
        List<QuoteVO> quoteVOS = new ArrayList<>();
        quoteVOS.add(new QuoteVO("BOC", "中国银行", "HKD", "CNY", 0.9196, 0.9161, LocalDateTime.now()));
        quoteVOS.add(new QuoteVO("AOC", "中国银行", "USD", "CNY", 0.9196, 0.9161, LocalDateTime.now()));
        when(quoteService.queryBankQuote(any())).thenReturn(quoteVOS);

        try {
            String requestBody = "{\n" +
                    "  \"tradeCcy\": \"HKD\",\n" +
                    "  \"swapCcy\": \"HKD\", \n" +
                    "  \"swapType\": \"SELL\"\n" +
                    "}";

            ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/quote/bank/query").content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[*].bankCode", is(Arrays.asList("BOC", "AOC"))));

            resultActions.andDo(result -> {
                log.info("result = {}", result);
            });
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }
}
