package com.luopc.platform.web.mds.enumaration;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Robin
 */

@Getter
public enum BankCodeMapping {
    /**
     * BankCodeMapping for IBank
     */
    BOC("中国银行"),
    ICBC("工商银行"),
    ABC("农业银行"),
    CCB("建设银行"),
    COMM("交通银行"),
    PSBC("邮政储蓄银行"),
    CMB("招商银行"),
    SPDB("浦发银行"),
    PINGAN("平安银行"),
    CEBBANK("光大银行"),
    CGBCHINA("广发银行"),
    HXB("华夏银行"),
    CZBANK("浙商银行"),
    BOSC("上海银行"),
    HSBC("汇丰银行"),
    ;
    private final String bankName;

    private static final Map<String, BankCodeMapping> BANK_CODES_MAP = Arrays.stream(BankCodeMapping.values()).collect(Collectors.toMap(BankCodeMapping::name, e -> e));

    BankCodeMapping(String bankName) {
        this.bankName = bankName;
    }

    public static BankCodeMapping phaseCode(String bankCode) {
        return BANK_CODES_MAP.get(bankCode);
    }
}
