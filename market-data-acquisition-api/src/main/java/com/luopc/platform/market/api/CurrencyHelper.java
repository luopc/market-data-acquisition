package com.luopc.platform.market.api;

/**
 * @author by Robin
 * @className CurrencyHelp
 * @date 2023/12/26 0026 23:34
 */
public class CurrencyHelper {

    /**
     * <a href="https://fififinance.com/currencies/ndf-currencies-list">ndf-currencies-list</a>
     **/
    public static final String[] NDF_GROUP = {"CNY", "INR", "MYR", "TWD", "KRW", "IDR", "PHP", "VND", "THB", "EGP", "KZT", "NGN", "ARS", "CLP", "CRC", "PEN", "COP", "UYU", "SRD", "VEF", "GTQ", "BRL"};
    public static final String[] G20_GROUP = {"JPY", "CHF", "CNH", "KRW", "SGD", "AUD", "EUR", "CAD", "GBP", "USD", "IDR", "SAR", "INR", "ZAR", "MXN", "BRL", "RUB", "TRY", "ARS"};
    public static final String[] CLS_GROUP = {"AUD", "CAD", "DKK", "EUR", "HKD", "HUF", "ILS", "JPY", "MXN", "NZD", "SGD", "ZAR", "KRW", "SEK", "CHF", "GBP", "USD"};
    public static final String[] TOP_TO_USD = {"AUD", "BWP", "EUR", "FJD", "GBP", "GIP", "GLD", "GLT", "MTL", "PGK", "TOP"};
    public static final String[] METAL_GROUP = {"XAG", "XAL", "XAU", "XCU", "XPD", "XRH", "XZN"};

    public static final Currency ALL = Currency.getInstance("ALL");
    public static final Currency USD = Currency.getInstance("USD");
    public static final Currency EUR = Currency.getInstance("EUR");
    public static final Currency JPY = Currency.getInstance("JPY");
    public static final Currency HKD = Currency.getInstance("HKD");
    public static final Currency GBP = Currency.getInstance("GBP");
    public static final Currency CNY = Currency.getInstance("CNY");


    /**
     * <a href="https://fififinance.com/currencies/ndf-currencies-list">NDF Currency Pairs</a>
     */
    public static final String[] NDF_CCY_PAIR_GROUP = {"CNY/USD", "INR/USD", "KRW/USD", "BRL/USD", "IDR/USD", "MYR/USD", "TWD/USD", "ARS/USD", "THB/USD", "PHP/USD", "NGN/USD"};

    public static final CcyPair EURUSD = CcyPair.getInstance("EURUSD");
    public static final CcyPair EURJPY = CcyPair.getInstance("EURJPY");
    public static final CcyPair GBPUSD = CcyPair.getInstance("GBPUSD");
    public static final CcyPair USDCNY = CcyPair.getInstance("USDCNY");
    public static final CcyPair EURCNY = CcyPair.getInstance("EURCNY");
    public static final CcyPair GBPCNY = CcyPair.getInstance("GBPCNY");
    public static final CcyPair EURGBP = CcyPair.getInstance("GBPEUR");
    public static final CcyPair EURAUD = CcyPair.getInstance("EURAUD");
    public static final CcyPair USDJPY = CcyPair.getInstance("USDJPY");
    public static final CcyPair JPYCNY = CcyPair.getInstance("JPYCNY");
    public static final CcyPair AUDCNY = CcyPair.getInstance("AUDCNY");
    public static final CcyPair GBPAUD = CcyPair.getInstance("GBPAUD");

    public static final MarketPrices EURUSD_PRICE = new MarketPrices(EURUSD, 1.12, 1.09);
    public static final MarketPrices EURJPY_PRICE = new MarketPrices(EURJPY, 158.65, 158.95);
    public static final MarketPrices GBPUSD_PRICE = new MarketPrices(GBPUSD, 1.2751, 1.2951);
    public static final MarketPrices USDCNY_PRICE = new MarketPrices(USDCNY, 7.17, 7.27);
    public static final MarketPrices EURCNY_PRICE = new MarketPrices(EURCNY, 7.85, 7.9921);
    public static final MarketPrices GBPCNY_PRICE = new MarketPrices(GBPCNY, 9.14, 9.84);
    public static final MarketPrices EURGBP_PRICE = new MarketPrices(EURGBP, 0.8583, 0.9083);
    public static final MarketPrices EURAUD_PRICE = new MarketPrices(EURAUD, 1.6374, 1.7874);
    public static final MarketPrices USDJPY_PRICE = new MarketPrices(USDJPY, 144.90, 145.12);
    public static final MarketPrices JPYCNY_PRICE = new MarketPrices(JPYCNY, 0.04945, 0.05145);
    public static final MarketPrices AUDCNY_PRICE = new MarketPrices(AUDCNY, 4.79, 5.01);
    public static final MarketPrices GBPAUD_PRICE = new MarketPrices(GBPAUD, 1.9068, 2.0038);

}
