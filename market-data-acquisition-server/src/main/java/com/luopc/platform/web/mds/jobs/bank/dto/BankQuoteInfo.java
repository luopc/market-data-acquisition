package com.luopc.platform.web.mds.jobs.bank.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * 现钞卖出价>现汇卖出价>现汇买入价>现钞买入价
 * 现钞买入价：指银行买入外币现钞、客户卖出外币现钞的价格。
 * 现汇买入价：指银行买入外汇、客户卖出外汇的价格. 外汇指由国外汇入或由境外携入、寄入的外币票据和凭证
 * 现钞卖出价：银行卖出外币现钞所使用的汇率。
 * 现汇卖出价：银行卖出外汇的价格。
 */
@Data
public class BankQuoteInfo {


    /**CNY*/
    private String baseCcy;
    /**交易货币*/
    private String quoteCcy;
    private String quoteCcyName;

    private String bankCode;
    private String bankName;

    /**现汇卖出价*/
    private Double exchangeSell;
    /**现汇买入价*/
    private Double exchangeBuy;
    /**现钞卖出价*/
    private Double cashSell;
    /**现钞买入价*/
    private Double cashBuy;
    /**中间价*/
    private Double middle;
    private Date upddate;

    @JSONField(name = "bankno")
    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    @JSONField(name = "banknm")
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    @JSONField(name = "se_sell")
    public void setExchangeSell(Double exchangeSell) {
        this.exchangeSell = exchangeSell;
    }

    @JSONField(name = "se_buy")
    public void setExchangeBuy(Double exchangeBuy) {
        this.exchangeBuy = exchangeBuy;
    }

    @JSONField(name = "cn_sell")
    public void setCashSell(Double cashSell) {
        this.cashSell = cashSell;
    }

    @JSONField(name = "cn_buy")
    public void setCashBuy(Double cashBuy) {
        this.cashBuy = cashBuy;
    }
}
