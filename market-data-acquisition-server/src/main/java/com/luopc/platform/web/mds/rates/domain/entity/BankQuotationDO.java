package com.luopc.platform.web.mds.rates.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author Robin
 */
@Data
@TableName("bank_quotation")
public class BankQuotationDO {

    @TableId
    private Long id;
    private String bankCode;
    private String baseCcy;
    private String quoteCcy;
    private Double exchangeSell;
    private Double exchangeBuy;
    private Double cashSell;
    private Double cashBuy;
    private Double middle;
    private Date updatedTime;

}
