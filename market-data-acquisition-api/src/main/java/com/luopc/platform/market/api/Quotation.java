package com.luopc.platform.market.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author Robin
 */
@Data
@ToString
public class Quotation {

    @Schema(description = "基础货币")
    private String baseCcy;
    @Schema(description = "交易货币符号")
    private String quoteCcy;

    @Schema(description = "现汇卖出价")
    private Double exchangeSell;
    @Schema(description = "现汇买入价")
    private Double exchangeBuy;
    @Schema(description = "现钞卖出价")
    private Double cashSell;
    @Schema(description = "现钞买入价")
    private Double cashBuy;
    @Schema(description = "中间价")
    private Double middle;
    @Schema(description = "最后更新时间")
    private LocalDateTime updateTime;


}
