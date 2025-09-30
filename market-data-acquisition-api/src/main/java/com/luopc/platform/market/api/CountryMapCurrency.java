package com.luopc.platform.market.api;

import lombok.Getter;

import java.util.*;

/**
 * @author by Robin
 * @className CountryMapCurrency
 * @date 2024/1/6 0006 23:21
 */
public class CountryMapCurrency {

    // 内部枚举类，用于表示国家地区-货币对
    @Getter
    public enum CountryCurrency {
        // 亚洲国家
        CHINA("中国", "CNY"),
        JAPAN("日本", "JPY"),
        SOUTH_KOREA("韩国", "KRW"),
        INDIA("印度", "INR"),
        INDONESIA("印度尼西亚", "IDR"),
        THAILAND("泰国", "THB"),
        SINGAPORE("新加坡", "SGD"),
        MALAYSIA("马来西亚", "MYR"),
        VIETNAM("越南", "VND"),
        PHILIPPINES("菲律宾", "PHP"),
        CAMBODIA("柬埔寨", "KHR"),
        LAOS("老挝", "LAK"),
        MYANMAR("缅甸", "MMK"),
        BANGLADESH("孟加拉国", "BDT"),
        NEPAL("尼泊尔", "NPR"),
        PAKISTAN("巴基斯坦", "PKR"),
        SRI_LANKA("斯里兰卡", "LKR"),
        MONGOLIA("蒙古", "MNT"),
        HONG_KONG("香港", "HKD"),
        MACAO("澳门", "MOP"),
        TAIWAN("台湾", "TWD"),

        // 欧洲国家
        EURO_ZONE("欧元区", "EUR"),
        EU("欧盟", "EUR"),
        UK("英国", "GBP"),
        RUSSIA("俄罗斯", "RUB"),
        SWITZERLAND("瑞士", "CHF"),
        SWEDEN("瑞典", "SEK"),
        NORWAY("挪威", "NOK"),
        DENMARK("丹麦", "DKK"),
        ICELAND("冰岛", "ISK"),
        POLAND("波兰", "PLN"),
        HUNGARY("匈牙利", "HUF"),
        CZECH_REPUBLIC("捷克共和国", "CZK"),
        ROMANIA("罗马尼亚", "RON"),
        BULGARIA("保加利亚", "BGN"),
        CROATIA("克罗地亚", "HRK"),
        SLOVAKIA("斯洛伐克", "EUR"),
        SLOVENIA("斯洛文尼亚", "EUR"),
        ALBANIA("阿尔巴尼亚", "ALL"),

        // 美洲国家
        USA("美国", "USD"),
        CANADA("加拿大", "CAD"),
        MEXICO("墨西哥", "MXN"),
        BRAZIL("巴西", "BRL"),
        ARGENTINA("阿根廷", "ARS"),
        CHILE("智利", "CLP"),
        COLOMBIA("哥伦比亚", "COP"),
        PERU("秘鲁", "PEN"),
        VENEZUELA("委内瑞拉", "VES"),
        ECUADOR("厄瓜多尔", "ECS"),
        BOLIVIA("玻利维亚", "BOB"),
        URUGUAY("乌拉圭", "UYU"),
        PARAGUAY("巴拉圭", "PYG"),
        CUBA("古巴", "CUP"),
        HONDURAS("洪都拉斯", "HNL"),
        GUATEMALA("危地马拉", "GTQ"),
        COSTA_RICA("哥斯达黎加", "CRC"),
        EL_SALVADOR("萨尔瓦多", "SVC"),
        NICARAGUA("尼加拉瓜", "NIO"),
        PANAMA("巴拿马", "PAB"),
        DOMINICAN_REPUBLIC("多米尼加", "DOP"),
        HAITI("海地", "HTG"),
        JAMAICA("牙买加", "JMD"),

        // 非洲国家
        SOUTH_AFRICA("南非", "ZAR"),
        EGYPT("埃及", "EGP"),
        NIGERIA("尼日利亚", "NGN"),
        KENYA("肯尼亚", "KES"),
        ETHIOPIA("埃塞俄比亚", "ETB"),
        MOROCCO("摩洛哥", "MAD"),
        ALGERIA("阿尔及利亚", "DZD"),
        TUNISIA("突尼斯", "TND"),
        GHANA("加纳", "GHS"),
        UGANDA("乌干达", "UGX"),
        TANZANIA("坦桑尼亚", "TZS"),
        MOZAMBIQUE("莫桑比克", "MZN"),
        ZIMBABWE("津巴布韦", "ZWL"),
        ZAMBIA("赞比亚", "ZMW"),
        MALAWI("马拉维", "MWK"),
        ANGOLA("安哥拉", "AOA"),

        // 大洋洲国家
        AUSTRALIA("澳大利亚", "AUD"),
        NEW_ZEALAND("新西兰", "NZD"),
        FIJI("斐济", "FJD"),

        // 中东国家
        SAUDI_ARABIA("沙特阿拉伯", "SAR"),
        UAE("阿联酋", "AED"),
        QATAR("卡塔尔", "QAR"),
        KUWAIT("科威特", "KWD"),
        BAHRAIN("巴林", "BHD"),
        OMAN("阿曼", "OMR"),
        JORDAN("约旦", "JOD"),
        LEBANON("黎巴嫩", "LBP"),
        IRAQ("伊拉克", "IQD"),
        IRAN("伊朗", "IRR"),
        ISRAEL("以色列", "ILS");

        private final String countryName;
        private final String currencyCode;

        CountryCurrency(String countryName, String currencyCode) {
            this.countryName = countryName;
            this.currencyCode = currencyCode;
        }

    }

    // 缓存映射关系，提高性能
    private static final Map<String, String> COUNTRY_TO_CURRENCY = new HashMap<>();

    // 静态初始化块，初始化映射关系
    static {
        // 初始化国家到货币的映射
        for (CountryCurrency cc : CountryCurrency.values()) {
            COUNTRY_TO_CURRENCY.put(cc.getCountryName(), cc.getCurrencyCode());
        }
        // 添加额外的映射关系
        addAdditionalMappings();
    }

    // 添加一些额外的映射关系
    private static void addAdditionalMappings() {
        Map<String, String> additionalMappings = new HashMap<>();
        additionalMappings.put("尼日利亚", "NGN");
        additionalMappings.put("卢旺达", "RWF");
        additionalMappings.put("几内亚", "GNF");
        additionalMappings.put("亚美尼亚", "AMD");
        additionalMappings.put("斯威士兰", "SZL");
        additionalMappings.put("文莱", "BND");
        additionalMappings.put("布隆迪", "BIF");
        additionalMappings.put("莱索托", "LSL");
        additionalMappings.put("纳米比亚", "NAD");
        additionalMappings.put("摩尔多瓦", "MDL");
        additionalMappings.put("波黑", "BAM");
        additionalMappings.put("乌兹别克斯坦", "UZS");
        additionalMappings.put("吉尔吉斯斯坦", "KGS");
        additionalMappings.put("塞舌尔", "SCR");
        additionalMappings.put("非洲金融共同体", "XOF");
        additionalMappings.put("毛里塔尼亚", "MRU");
        additionalMappings.put("马尔代夫", "MVR");
        additionalMappings.put("白俄罗斯", "BYN");
        additionalMappings.put("科摩罗", "KMF");
        additionalMappings.put("土耳其", "TRY");
        additionalMappings.put("苏丹", "SDG");
        additionalMappings.put("圭亚那", "GYD");
        additionalMappings.put("利比亚", "LYD");
        additionalMappings.put("巴巴多斯", "BBD");
        additionalMappings.put("马其顿", "MKD");
        additionalMappings.put("佛得角", "CVE");
        additionalMappings.put("格鲁吉亚", "GEL");
        additionalMappings.put("毛里求斯", "MUR");
        additionalMappings.put("塞尔维亚", "RSD");
        additionalMappings.put("博茨瓦纳", "BWP");
        additionalMappings.put("不丹", "BTN");
        additionalMappings.put("刚果", "CDF");
        additionalMappings.put("哈萨克斯坦", "KZT");
        additionalMappings.put("塞拉利昂", "SLL");
        additionalMappings.put("阿塞拜疆", "AZN");
        additionalMappings.put("特立尼达多巴哥", "TTD");
        additionalMappings.put("乌克兰", "UAH");
        additionalMappings.put("塔吉克斯坦", "TJS");
        additionalMappings.put("巴布亚新几内亚", "PGK");
        additionalMappings.put("伯利兹", "BZD");
        additionalMappings.put("圣多美和普林西比", "STD");
        additionalMappings.put("马达加斯加", "MGA");
        additionalMappings.put("冈比亚", "GMD");
        additionalMappings.put("巴哈马元", "BSD");
        additionalMappings.put("中非共和国", "XAF");
        additionalMappings.put("乍得", "XAF");
        additionalMappings.put("赤道几内亚", "GNF");
        additionalMappings.put("加蓬", "XAF");
        additionalMappings.put("贝宁", "XOF");
        additionalMappings.put("布基纳法索", "XOF");
        additionalMappings.put("几内亚比绍", "XOF");
        additionalMappings.put("马里", "MYR");
        additionalMappings.put("塞内加尔", "XOF");
        additionalMappings.put("多哥", "XOF");
        additionalMappings.put("利比里亚", "LRD");
        additionalMappings.put("新喀里多尼亚", "CFP");
        additionalMappings.put("苏里南", "SRD");
        additionalMappings.put("喀麦隆", "XAF");
        additionalMappings.put("瑞士-WIR欧元", "CHW");
        additionalMappings.put("瑞士-WIR法郎", "CHE");

        // 将额外的映射添加到缓存中
        COUNTRY_TO_CURRENCY.putAll(additionalMappings);
    }

    /**
     * 获取国家和货币的映射关系
     * @return 国家名称到货币代码的映射
     */
    public static Map<String, String> getCountryAndCcyMapping() {
        return Collections.unmodifiableMap(COUNTRY_TO_CURRENCY);
    }

}
