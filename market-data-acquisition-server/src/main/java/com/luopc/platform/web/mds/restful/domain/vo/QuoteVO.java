package com.luopc.platform.web.mds.restful.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Robin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuoteVO {

    @Schema(description = "银行ID")
    private String bankCode;
    @Schema(description = "银行名称")
    private String bankName;
    @Schema(description = "baseCcy")
    private String baseCcy;
    @Schema(description = "quoteCcy")
    private String quoteCcy;
    @Schema(description = "买入价")
    private Double buy;
    @Schema(description = "卖出价")
    private Double sell;
    @Schema(description = "中间汇率")
    private Double middle;
    @Schema(description = "最后更新时间")
    private LocalDateTime lastUpdateTime;

    public QuoteVO(String bankCode, String bankName, String baseCcy, String quoteCcy, Double buy, Double sell, LocalDateTime lastUpdateTime) {
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.baseCcy = baseCcy;
        this.quoteCcy = quoteCcy;
        this.buy = buy;
        this.sell = sell;
        this.lastUpdateTime = lastUpdateTime;
    }
}
