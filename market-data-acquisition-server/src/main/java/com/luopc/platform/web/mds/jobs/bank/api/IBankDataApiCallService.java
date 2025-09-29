package com.luopc.platform.web.mds.jobs.bank.api;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.google.common.collect.Lists;
import com.luopc.platform.market.api.ExecutingBankEnum;
import com.luopc.platform.web.mds.config.EconomicsApiConfig;
import com.luopc.platform.web.mds.enumaration.BankCodeMapping;
import com.luopc.platform.web.mds.jobs.bank.dto.IBankData;
import com.luopc.platform.web.mds.rates.domain.dto.BankQuotation;
import jakarta.annotation.Resource;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * @author Robin
 */
@Slf4j
@Setter
@Service
@NoArgsConstructor
public class IBankDataApiCallService {

    @Resource
    private EconomicsApiConfig economicsApiConfig;

    public List<BankQuotation> getExchangeQuotationFromAPI() {
        List<IBankData> iBankDataList = getQuotesFromAPI();
        return iBankDataList.stream().filter(iBankData -> StringUtils.hasLength(iBankData.getCcy())).map(this::convertToBankQuote).collect(Collectors.toList());
    }

    private BankQuotation convertToBankQuote(IBankData iBankData) {
        BankQuotation exchangeQuotation = new BankQuotation();
        exchangeQuotation.setBaseCcy("CNY");
        exchangeQuotation.setQuoteCcy(iBankData.getCcy());
        exchangeQuotation.setQuoteCcyName(iBankData.getCcyName());
        BeanUtil.copyProperties(iBankData, exchangeQuotation);
        String bankCode = iBankData.getBankCode();
        ExecutingBankEnum executingBankEnum = convertToExecutingBank(bankCode);
        exchangeQuotation.setBankCode(executingBankEnum.getCode());
        exchangeQuotation.setBankName(executingBankEnum.getName());
        if (Objects.nonNull(iBankData.getUpddate())) {
            exchangeQuotation.setUpdateTime(LocalDateTime.ofInstant(iBankData.getUpddate().toInstant(), ZoneId.systemDefault()));
        }
        return exchangeQuotation;
    }

    private ExecutingBankEnum convertToExecutingBank(String bankCode) {
        BankCodeMapping bankCodeMapping = BankCodeMapping.phaseCode(bankCode);
        switch (bankCodeMapping) {
            case ICBC:
                return ExecutingBankEnum.ICBC;
            case ABC:
                return ExecutingBankEnum.ABCC;
            case CCB:
                return ExecutingBankEnum.CCBC;
            case COMM:
                return ExecutingBankEnum.COMM;
            case PSBC:
                return ExecutingBankEnum.PSBC;
            case CMB:
                return ExecutingBankEnum.CMBC;
            case SPDB:
                return ExecutingBankEnum.SPDB;
            case PINGAN:
                return ExecutingBankEnum.PABC;
            case CEBBANK:
                return ExecutingBankEnum.CEBB;
            case CGBCHINA:
                return ExecutingBankEnum.CGBC;
            case HXB:
                return ExecutingBankEnum.HXBC;
            case CZBANK:
                return ExecutingBankEnum.CZBK;
            case BOSC:
                return ExecutingBankEnum.BOSC;
            case HSBC:
                return ExecutingBankEnum.HSBC;
            case BOC:
            default:
                return ExecutingBankEnum.BOCC;
        }

    }

    private List<IBankData> getQuotesFromAPI() {
        //http://www.cnhuilv.com/bank/cmb/
        String url = economicsApiConfig.getBankQuoteUrl();

        List<IBankData> bankDataList = new ArrayList<>();
        if (StringUtils.hasLength(url)) {
            Arrays.stream(BankCodeMapping.values()).forEach(bankCode -> {
                List<IBankData> quotesFromAPI = getQuotesFromAPI(url, bankCode);
                if (CollectionUtils.isNotEmpty(quotesFromAPI)) {
                    bankDataList.addAll(quotesFromAPI);
                }
            });
        }

        return bankDataList;
    }

    private List<IBankData> getQuotesFromAPI(String url, BankCodeMapping bankCode) {
        String finalUrl = url + bankCode.name().toLowerCase();
        //获取连接
        Connection con = Jsoup.connect(finalUrl);
        try {

            //选择发送方式,获取整个网页信息，存入 documnet
            Document document = con.get();
            //通过class属性 ，获取子类元素

            Elements tables = document.body().getElementsByClass("table-responsive");
            int unit = 1;
            try {
                Element unitDiv = document.body().getElementsByClass("hlinfoflags").get(0);
                String unitStr = unitDiv.text();
                Pattern ccyPattern = Pattern.compile("[0-9]+");
                Matcher matcher = ccyPattern.matcher(unitStr);
                if (matcher.find()) {
                    unit = Integer.parseInt(matcher.group(0).trim());
                }
            } catch (Exception e) {
                // ignore
            }
            if (!tables.isEmpty()) {
                Element pTable = tables.get(0);
                Elements tBody = pTable.getElementsByTag("tbody");
                if (!tBody.isEmpty()) {
                    Elements trs = tBody.get(0).children();

                    List<String> heads = Lists.newArrayList();
                    List<Map<String, String>> bodies = Lists.newArrayList();

                    trs.forEach(tr -> {
                        boolean isHead = heads.isEmpty();
                        if (!tr.children().isEmpty()) {
                            Elements tdList = tr.getElementsByTag("td");
                            Map<String, String> valuesMap = new HashMap<>();
                            for (int i = 0; i < tdList.size(); i++) {
                                String value = tdList.get(i).text();
                                if (isHead) {
                                    heads.add(value);
                                } else {
                                    valuesMap.put(heads.get(i), value);
                                }
                            }
                            if (!valuesMap.isEmpty()) {
                                bodies.add(valuesMap);
                            }

                        }
                    });
                    if (!bodies.isEmpty()) {
                        //log.debug("data from API = {}", bodies);

                        String jsonString = JSONObject.toJSONString(bodies);
                        List<IBankData> bankDataList = JSON.parseArray(jsonString, IBankData.class);
                        int finalUnit = unit;
                        List<IBankData> resultList = bankDataList.stream().peek(iBankData -> {
                            iBankData.setUnit(finalUnit);
                            iBankData.setBankCode(bankCode.name());
                            iBankData.setBankName(bankCode.getBankName());
                            //log.debug("Bank code = {}, Bank name = {}, ccy = {}", iBankData.getBankCode(), iBankData.getName(), iBankData.getCcy());
                        }).collect(Collectors.toList());
                        //log.debug("data after converting = {}\n", resultList);
                        return resultList;
                    }
                }

            }
        } catch (SocketTimeoutException e) {
            log.error("Url [{}] is unavailable now.", url);
        } catch (IOException e) {
            log.error("Unable to retrieve data from API, url = {}", url, e);
        }
        return null;
    }
}
