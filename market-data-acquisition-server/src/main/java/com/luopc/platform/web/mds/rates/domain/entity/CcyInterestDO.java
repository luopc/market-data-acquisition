package com.luopc.platform.web.mds.rates.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Robin
 */
@Data
@TableName("ccy_interest")
public class CcyInterestDO implements Serializable {

    @TableId
    private String ccy;
    private String countryName;
    private Double rate;
    private Double preRate;
    private Date updatedTime;

}
