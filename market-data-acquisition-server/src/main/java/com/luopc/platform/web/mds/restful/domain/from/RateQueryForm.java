package com.luopc.platform.web.mds.restful.domain.from;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;


/**
 * @author Robin
 */
@Data
public class RateQueryForm {

    @Schema(description  = "priCcy")
    @Size(min = 3, max = 3, message = "货币长度必须是3")
    private String priCcy;
    @Schema(description  = "cntCcy")
    @Size(min = 3, max = 3, message = "货币长度必须是3")
    private String cntCcy;
}
