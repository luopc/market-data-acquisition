package com.luopc.platform.web.mds.restful.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Robin
 */
@Data
@AllArgsConstructor
public class RateVO {

    @Schema(description = "priCcy")
    private String priCcy;
    @Schema(description = "cntCcy")
    private String cntCcy;
    @Schema(description = "汇率")
    private Double rate;
    @Schema(description = "最后更新时间")
    private LocalDateTime lastUpdateTime;

    public RateVO() {
    }

    public RateVO(String priCcy, String cntCcy) {
        this.priCcy = priCcy;
        this.cntCcy = cntCcy;
    }
}
