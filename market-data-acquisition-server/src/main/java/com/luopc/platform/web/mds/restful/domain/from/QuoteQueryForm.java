package com.luopc.platform.web.mds.restful.domain.from;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;


/**
 * @author Robin
 */
@Data
public class QuoteQueryForm {

    @Schema(description  = "基础货币")
    @Size(min = 3, max = 3, message = "货币长度必须是3")
    private String baseCcy;
    @Schema(description = "需要兑换的货币")
    @Size(min = 3, max = 3, message = "货币长度必须是3")
    private String quoteCcy;
    @Schema(description = "forward day: from Tenor.java")
    private String tenor;
    @Schema(description = "forward day: from Tenor.java")
    private String valueDate;

}
