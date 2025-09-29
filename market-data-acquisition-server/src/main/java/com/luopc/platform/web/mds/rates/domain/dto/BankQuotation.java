package com.luopc.platform.web.mds.rates.domain.dto;

import com.luopc.platform.market.api.Quotation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

/**
 * 现钞卖出价>现汇卖出价>现汇买入价>现钞买入价
 * 现钞买入价：指银行买入外币现钞、客户卖出外币现钞的价格。
 * 现汇买入价：指银行买入外汇、客户卖出外汇的价格. 外汇指由国外汇入或由境外携入、寄入的外币票据和凭证
 * 现钞卖出价：银行卖出外币现钞所使用的汇率。
 * 现汇卖出价：银行卖出外汇的价格。
 * @author Robin
 */

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BankQuotation extends Quotation {

    @Schema(description = "银行代码")
    private String bankCode;
    @Schema(description = "银行名称")
    private String bankName;

    @Schema(description = "交易货币名称")
    private String quoteCcyName;

}
