package com.luopc.platform.web.mds.jobs.bank.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.luopc.platform.market.tools.RateCalculator;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 现钞卖出价>现汇卖出价>现汇买入价>现钞买入价
 * 现钞买入价：指银行买入外币现钞、客户卖出外币现钞的价格。
 * 现汇买入价：指银行买入外汇、客户卖出外汇的价格. 外汇指由国外汇入或由境外携入、寄入的外币票据和凭证
 * 现钞卖出价：银行卖出外币现钞所使用的汇率。
 * 现汇卖出价：银行卖出外汇的价格。
 * https://mp.weixin.qq.com/s/999whlHkpuJSXYt-OrIVRQ
 *
 * @author Robin
 */
@Data
@Slf4j
public class IBankData {

    private String bankCode;
    private String bankName;

    private String name;
    private int unit = 1;
    private String exchangeSell;
    private String exchangeBuy;
    private String cashSell;
    private String cashBuy;
    private String middle;
    private Date upddate = new Date();


    public String getCcy() {
        Pattern ccyPattern = Pattern.compile("([A-Z]{3})+[ ]");
        Matcher matcher = ccyPattern.matcher(name);
        if (matcher.find()) {
            return matcher.group(0).trim();
        }
        return null;
    }

    public String getCcyName() {
        Pattern ccyPattern = Pattern.compile("([A-Z]{3})+[ ]");
        Matcher matcher = ccyPattern.matcher(name);
        if (matcher.find()) {
            String ccyCode = matcher.group(0).trim();
            return name.replace(ccyCode, "").trim();
        }
        return null;
    }

    public double getDoubleRateValue(String rate) {
        try {
            if ("JPY".equals(getCcy())) {
                double ccyRate = RateCalculator.div(new BigDecimal(rate), BigDecimal.valueOf(unit), RateCalculator.RATE_SCALE).doubleValue();
                if (ccyRate > 1) {
                    return RateCalculator.div(new BigDecimal(ccyRate), BigDecimal.valueOf(100)).doubleValue();
                } else {
                    return ccyRate;
                }
            } else {
                return RateCalculator.div(new BigDecimal(rate), BigDecimal.valueOf(unit), RateCalculator.RATE_SCALE).doubleValue();
            }
        } catch (Exception e) {
            return 0d;
        }
    }

    public double getExchangeSell() {
        return getDoubleRateValue(exchangeSell);
    }

    public double getExchangeBuy() {
        return getDoubleRateValue(exchangeBuy);
    }

    public double getCashSell() {
        return getDoubleRateValue(cashSell);
    }

    public double getCashBuy() {
        return getDoubleRateValue(cashBuy);
    }

    public double getMiddle() {
        return getDoubleRateValue(middle);
    }

    @JSONField(name = "货币名称")
    public void setName(String name) {
        this.name = name;
    }

    @JSONField(name = "币种")
    public void setName2(String name) {
        this.name = name;
    }

    @JSONField(name = "货币")
    public void setName3(String name) {
        this.name = name;
    }

    @JSONField(name = "汇卖价")
    public void setExchangeSell(String exchangeSell) {
        this.exchangeSell = exchangeSell;
    }

    @JSONField(name = "现汇卖出价")
    public void setExchangeSell2(String seSell) {
        this.exchangeSell = seSell;
    }

    @JSONField(name = "汇买价")
    public void setExchangeBuy(String exchangeBuy) {
        this.exchangeBuy = exchangeBuy;
    }

    @JSONField(name = "现汇买入价")
    public void setExchangeBuy2(String seBuy) {
        this.exchangeBuy = seBuy;
    }

    @JSONField(name = "买入价（汇）")
    public void setExchangeBuy3(String seBuy) {
        this.exchangeBuy = seBuy;
    }

    @JSONField(name = "买入价")
    public void setExchangeBuy4(String seBuy) {
        this.exchangeBuy = seBuy;
    }

    @JSONField(name = "钞卖价")
    public void setCashSell(String cashSell) {
        this.cashSell = cashSell;
    }

    @JSONField(name = "现钞卖出价")
    public void setCashSell2(String cnSell) {
        this.cashSell = cnSell;
    }

    @JSONField(name = "卖出价")
    public void setCashSell3(String cnSell) {
        this.cashSell = cnSell;
    }

    @JSONField(name = "钞买价")
    public void setCashBuy(String cashBuy) {
        this.cashBuy = cashBuy;
    }

    @JSONField(name = "现钞买入价")
    public void setCashBuy2(String cnBuy) {
        this.cashBuy = cnBuy;
    }

    @JSONField(name = "买入价（钞）")
    public void setCashBuy3(String cnBuy) {
        this.cashBuy = cnBuy;
    }


    @JSONField(name = "中行折算价")
    public void setMiddle(String middle) {
        this.middle = middle;
    }

    @JSONField(name = "基准汇率")
    public void setMiddle2(String middle) {
        this.middle = middle;
    }

    @JSONField(name = "邮储银行折算价")
    public void setMiddle3(String middle) {
        this.middle = middle;
    }

    @JSONField(name = "中间价")
    public void setMiddle4(String middle) {
        this.middle = middle;
    }

    @JSONField(name = "现汇中间价")
    public void setMiddle5(String middle) {
        this.middle = middle;
    }

    @JSONField(name = "更新日期", format = "yyyy-MM-dd")
    public void setUpddate(Date upddate) {
        this.upddate = upddate;
    }

    @JSONField(name = "发布日期", format = "yyyy.MM.dd HH:mm:ss")
    public void setUpddate1(Date upddate) {
        this.upddate = upddate;
    }

    @JSONField(name = "发布时间", format = "HH:mm:ss")
    public void setUpddate2(String upddate) {
        this.upddate = new Date();
    }

    @JSONField(name = "时间", format = "HH:mm:ss")
    public void setUpddate3(String upddate) {
        this.upddate = new Date();
    }


}
