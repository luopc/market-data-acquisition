package com.luopc.platform.web.mds.rates.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author Robin
 */
@Data
@TableName("currency_mapping")
public class CurrencyMappingDO {

    @TableId
    private Long id;
    private String ccy;
    private Integer ccyNum;
    private String currencyName;
    private String countryName;
    private Boolean delivery;
    private Date updatedTime;

}
