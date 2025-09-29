package com.luopc.platform.web.mds.jobs.rates.dto.rates;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author by Robin
 * @className ExchangeRate
 * @description TODO
 * @date 2024/1/6 0006 19:49
 */
@NoArgsConstructor
@Data
public class ExchangeRate {

    @JSONField(name = "vrtCode")
    private String vrtCode;
    @JSONField(name = "price")
    private String price;
    @JSONField(name = "vrtName")
    private String vrtName;
    @JSONField(name = "vrtEName")
    private String vrtEnName;
    @JSONField(name = "foreignCName")
    private String foreignCnName;

}
