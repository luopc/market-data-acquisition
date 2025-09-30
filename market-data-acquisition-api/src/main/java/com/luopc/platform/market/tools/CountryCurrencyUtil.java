package com.luopc.platform.market.tools;

import com.luopc.platform.market.api.CountryMapCurrency;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 国家/地区与货币代码双向映射工具类
 * 注意：台湾、香港、澳门是中国的地区，不是国家，此处按原有数据保留映射关系
 */
public class CountryCurrencyUtil {

    // 国家/地区到货币代码的映射
    private static final Map<String, String> COUNTRY_TO_CURRENCY;
    // 货币代码到国家/地区的映射（可能一对多，所以值为Set）
    private static final Map<String, Set<String>> CURRENCY_TO_COUNTRIES;

    static {
        // 初始化国家到货币的映射（复用原有映射关系）
        COUNTRY_TO_CURRENCY = CountryMapCurrency.getCountryAndCcyMapping();

        // 初始化货币到国家的映射
        CURRENCY_TO_COUNTRIES = new HashMap<>();
        for (Map.Entry<String, String> entry : COUNTRY_TO_CURRENCY.entrySet()) {
            String country = entry.getKey();
            String currency = entry.getValue();
            // 如果当前货币代码还没有对应的集合，则创建一个新的
            CURRENCY_TO_COUNTRIES.computeIfAbsent(currency, k -> new java.util.HashSet<>())
                    .add(country);
        }
    }

    /**
     * 根据国家/地区名称获取货币代码
     *
     * @param countryName 国家/地区名称（如"中国"、"香港"）
     * @return 对应的货币代码，若未找到则返回null
     */
    public static String getCurrencyByCountry(String countryName) {
        return COUNTRY_TO_CURRENCY.get(countryName);
    }

    /**
     * 根据货币代码获取对应的所有国家/地区名称
     *
     * @param currencyCode 货币代码（如"CNY"、"HKD"）
     * @return 对应的国家/地区名称集合，若未找到则返回空集合
     */
    public static Set<String> getCountriesByCurrency(String currencyCode) {
        // 返回不可修改的集合，避免外部修改内部数据
        return CURRENCY_TO_COUNTRIES.getOrDefault(currencyCode, new java.util.HashSet<>());
    }

    /**
     * 检查是否包含指定的国家/地区
     *
     * @param countryName 国家/地区名称
     * @return 存在返回true，否则返回false
     */
    public static boolean containsCountry(String countryName) {
        return COUNTRY_TO_CURRENCY.containsKey(countryName);
    }

    /**
     * 检查是否包含指定的货币代码
     *
     * @param currencyCode 货币代码
     * @return 存在返回true，否则返回false
     */
    public static boolean containsCurrency(String currencyCode) {
        return CURRENCY_TO_COUNTRIES.containsKey(currencyCode);
    }


    /**
     * 获取所有国家名称
     *
     * @return 所有国家名称的集合
     */
    public static Set<String> getAllCountries() {
        return Collections.unmodifiableSet(COUNTRY_TO_CURRENCY.keySet());
    }

    /**
     * 获取所有货币代码
     *
     * @return 所有货币代码的集合
     */
    public static Set<String> getAllCurrencies() {
        return Collections.unmodifiableSet(CURRENCY_TO_COUNTRIES.keySet());
    }
}
