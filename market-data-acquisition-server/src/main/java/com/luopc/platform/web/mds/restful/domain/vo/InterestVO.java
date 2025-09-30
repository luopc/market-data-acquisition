package com.luopc.platform.web.mds.restful.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterestVO {

    @Schema(description = "ccy")
    private String ccy;
    @Schema(description = "国家或地区名")
    private String ccyName;
    @Schema(description = "汇率")
    private Double rate;

}
