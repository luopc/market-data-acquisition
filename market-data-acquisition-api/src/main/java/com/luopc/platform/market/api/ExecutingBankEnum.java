package com.luopc.platform.market.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Robin
 */

@Getter
@AllArgsConstructor
public enum ExecutingBankEnum {
    /**
     *
     */
    PSBC("PSBCCHBK", "Postal Savings Bank", "邮政储蓄银行", "Postal Savings Bank of China"),
    ICBC("ICBCCHBK", "Industrial And Commercial Bank", "工商银行", "Industrial And Commercial Bank of China"),
    ABCC("ABCCCHBK", "Agricultural Bank of China", "农业银行", "Agricultural Bank of China"),
    CMBC("CMBCCHBK", "China Merchants Bank", "招商银行", "China Merchants Bank"),
    CZBK("CZBKCHBK", "China Zhe Shang Bank", "浙商银行", "China Zheshang Bank Co., Ltd"),
    CGBC("CGBCCHBK", "Guang Fa Bank", "广发银行", "China Guangfa Bank"),
    PABC("PABCCHBK", "Ping An Bank", "平安银行", "Ping An Bank Co., Ltd."),
    CCBC("CCBCCHBK", "Construction Bank", "建设银行", "China Construction Bank"),
    COMM("COMMCHBK", "Bank of Communications", "交通银行", "Bank of Communications"),
    HXBC("HXBCCHBK", "Hua Xia Bank", "华夏银行", "Hua Xia Bank Co., Ltd"),
    BOCC("BOCCCHBK", "BANK OF CHINA", "中国银行", "Bank of China"),
    CEBB("CEBBCHBK", "China Ever Bright BANK", "光大银行", "China Ever bright Bank Co., Ltd"),
    SPDB("SPDBCHBK", "Shanghai Pu Dong Bank", "浦发银行", "Shanghai Pudong Development Bank"),
    BOSC("BOSCCHBK", "Shanghai Bank", "上海银行", "Bank Of Shanghai"),
    JPMB("JPMBUSBK", "J.P.Morgan", "摩根银行", "J.P.Morgan "),
    CITI("CITIUSBK", "Citibank", "花旗银行", "Citi Bank"),
    DKB("DIKBJPBK", "Dai-Ichi Kangyo Bank", "第一劝业银行", "Dai-Ichi Kangyo Bank"),
    FUJI("FUJIJPBK", "Fuji Bank", "富士银行", "Fuji Bank"),
    HSBC("HSBCLDBK", "Hongkong and Shanghai Bank", "汇丰银行", "Hongkong and Shanghai Banking Corp"),
    ;

    @Schema(description = "Bank code of ExecutingBank")
    private final String code;
    @Schema(description = "Bank name of ExecutingBank")
    private final String name;
    @Schema(description = "Bank chinese name of ExecutingBank")
    private final String cnName;
    @Schema(description = "Bank business name of ExecutingBank")
    private final String buzName;

}
