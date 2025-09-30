package com.luopc.platform.market.mapping;

import java.util.HashMap;
import java.util.Map;

/**
 * 国家/地区、货币名称、货币符号映射工具类
 */
public class CurrencyInfoMapping {

    // 存储格式：国家/地区 -> 货币信息（格式："货币名称,货币符号,货币代码"）
    private static final Map<String, String> CURRENCY_INFO_MAP;

    static {
        CURRENCY_INFO_MAP = new HashMap<>();
        CURRENCY_INFO_MAP.put("中国", "人民币,¥,CNY");
        CURRENCY_INFO_MAP.put("中国香港", "港元,$,HKD");
        CURRENCY_INFO_MAP.put("中国澳门", "澳门元,P, MOP");
        CURRENCY_INFO_MAP.put("美国", "美元,$,USD");
        CURRENCY_INFO_MAP.put("欧元区", "欧元,€,EUR");
        CURRENCY_INFO_MAP.put("英国", "英镑,£,GBP");
        CURRENCY_INFO_MAP.put("日本", "日元,¥,JPY");
        CURRENCY_INFO_MAP.put("韩国", "韩元,₩,KRW");
        CURRENCY_INFO_MAP.put("新加坡", "新加坡元,$,SGD");
        CURRENCY_INFO_MAP.put("泰国", "泰铢,฿,THB");
        CURRENCY_INFO_MAP.put("印度", "印度卢比,₹,INR");
        CURRENCY_INFO_MAP.put("澳大利亚", "澳大利亚元,$,AUD");
        CURRENCY_INFO_MAP.put("加拿大", "加拿大元,$,CAD");
        CURRENCY_INFO_MAP.put("瑞士", "瑞士法郎,Fr,CHF");
        CURRENCY_INFO_MAP.put("俄罗斯", "俄罗斯卢布,₽,RUB");
        CURRENCY_INFO_MAP.put("巴西", "巴西雷亚尔,R$,BRL");
        CURRENCY_INFO_MAP.put("南非", "南非兰特,R,ZAR");
        CURRENCY_INFO_MAP.put("沙特阿拉伯", "沙特里亚尔,﷼,SAR");
        CURRENCY_INFO_MAP.put("阿联酋", "阿联酋迪拉姆,د.إ,AED");
        CURRENCY_INFO_MAP.put("土耳其", "土耳其里拉,₺,TRY");
        CURRENCY_INFO_MAP.put("墨西哥", "墨西哥比索,$,MXN");
        // 更多国家/地区数据可根据链接内容继续补充
    }

    /**
     * 根据国家/地区获取货币信息
     * @param country 国家/地区名称
     * @return 货币信息数组 [货币名称, 货币符号, 货币代码]，未找到则返回null
     */
    public static String[] getCurrencyInfoByCountry(String country) {
        String info = CURRENCY_INFO_MAP.get(country);
        return info != null ? info.split(",") : null;
    }

    /**
     * 根据国家/地区获取货币名称
     * @param country 国家/地区名称
     * @return 货币名称，未找到则返回null
     */
    public static String getCurrencyNameByCountry(String country) {
        String[] info = getCurrencyInfoByCountry(country);
        return info != null ? info[0] : null;
    }

    /**
     * 根据国家/地区获取货币符号
     * @param country 国家/地区名称
     * @return 货币符号，未找到则返回null
     */
    public static String getCurrencySymbolByCountry(String country) {
        String[] info = getCurrencyInfoByCountry(country);
        return info != null ? info[1] : null;
    }

    /**
     * 根据国家/地区获取货币代码
     * @param country 国家/地区名称
     * @return 货币代码，未找到则返回null
     */
    public static String getCurrencyCodeByCountry(String country) {
        String[] info = getCurrencyInfoByCountry(country);
        return info != null ? info[2] : null;
    }
}
