package com.luopc.platform.web.mds.rates.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.luopc.platform.market.api.CcyPair;
import lombok.Data;

/**
 * @author Robin
 */
@Data
@TableName("market_quotation")
public class MarketQuotationDO {
    @TableId
    private Long id;
    private String baseCcy;
    private String quoteCcy;
    private Double buy;
    private Double sell;
    private String lastPrices;
}
