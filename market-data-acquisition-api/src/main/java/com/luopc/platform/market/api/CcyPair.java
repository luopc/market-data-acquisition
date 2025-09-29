package com.luopc.platform.market.api;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Robin
 */
@Getter
public class CcyPair {

    private final String ccyPairStr;

    @JSONField(serialize = false)
    private final Currency ccy1;
    @JSONField(serialize = false)
    private final Currency ccy2;
    @JSONField(serialize = false)
    private static final Map<String, CcyPair> STRING_TO_CCY_PAIR = new ConcurrentHashMap<>();


    private CcyPair(Currency ccy1, Currency ccy2, String ccyPairStr) {
        this.ccy1 = ccy1;
        this.ccy2 = ccy2;
        this.ccyPairStr = ccyPairStr;
    }

    public static CcyPair getInstance(String ccy1, String ccy2) {
        return createNewCcyPair(ccy1, ccy2);
    }

    public static CcyPair getInstance(Currency ccy1, Currency ccy2) {
        return createNewCcyPair(ccy1, ccy2);
    }

    public static CcyPair getInstance(String pairStr) {
        if (Objects.isNull(pairStr)) {
            throw new IllegalArgumentException("Illegal to create ccy pair instance from empty string: " + pairStr);
        }

        pairStr = pairStr.toLowerCase();
        String ccy1Str = null;
        String ccy2Str = null;
        if (pairStr.length() == 6) {
            ccy1Str = pairStr.substring(0, 3);
            ccy2Str = pairStr.substring(3, 6);
        } else if (pairStr.length() == 7) {
            ccy1Str = pairStr.substring(0, 3);
            ccy2Str = pairStr.substring(4, 7);
        } else {
            throw new IllegalArgumentException("Illegal to create ccy pair instance from empty string: " + pairStr);
        }
        if (STRING_TO_CCY_PAIR != null) {
            if (STRING_TO_CCY_PAIR.containsKey(pairStr)) {
                return STRING_TO_CCY_PAIR.get(pairStr);
            } else {
                return createNewCcyPair(ccy1Str, ccy2Str);
            }
        }
        throw new IllegalArgumentException("Illegal to create ccy pair instance from empty string: " + pairStr);
    }

    private static CcyPair createNewCcyPair(String ccy1Str, String ccy2Str) {
        return createNewCcyPair(Currency.getInstance(ccy1Str), Currency.getInstance(ccy2Str));
    }

    private static CcyPair createNewCcyPair(Currency ccy1Str, Currency ccy2Str) {
        String pairStr = ccy1Str.getCcyCode() + "/" + ccy2Str.getCcyCode();
        CcyPair ccyPair = new CcyPair(ccy1Str, ccy2Str, pairStr);
        CcyPair previous = STRING_TO_CCY_PAIR.putIfAbsent(pairStr, ccyPair);
        if (previous != null) {
            return previous;
        }
        return ccyPair;
    }

    @JSONField(serialize = false)
    public Currency getNonUsdCcy() {
        if (hasUsd()) {
            return ccy1.isUsdFlag() ? ccy1 : ccy2;
        }
        return null;
    }

    @JSONField(serialize = false)
    public boolean hasUsd() {
        return ccy1.isUsdFlag() || ccy2.isUsdFlag();
    }

    @JSONField(serialize = false)
    public boolean isNdfFlag() {
        String pairReversal = ccy2.getCcyCode() + "/" + ccy1.getCcyCode();
        return Arrays.asList(CurrencyHelper.NDF_CCY_PAIR_GROUP).contains(ccyPairStr) || Arrays.asList(CurrencyHelper.NDF_CCY_PAIR_GROUP).contains(pairReversal);
    }

    @JSONField(serialize = false)
    public boolean isClsCcyPair() {
        return ccy1.isClsFlag() && ccy2.isClsFlag();
    }

    @JSONField(serialize = false)
    public boolean isG20CcyPair() {
        return ccy1.isG20Flag() && ccy2.isG20Flag();
    }

    @JSONField(serialize = false)
    public int getSpotDays() {
        return Math.max(ccy1.getSpotDays(), ccy2.getSpotDays());
    }

    @Override
    public String toString() {
        return ccyPairStr;
    }
}
