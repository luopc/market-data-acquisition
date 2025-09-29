package com.luopc.platform.web.mds.rates.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author Robin
 */
@Data
@TableName("spot_rate")
public class SpotRateDO {

    @TableId
    private Long id;
    private String ccy1;
    private String ccy2;
    private String nonUsdCcy;
    private Double rate;
    private Double useConversion;
    private Date snapshotDate;
    private Boolean eodFlag = false;
    private Date updatedTime;

}
