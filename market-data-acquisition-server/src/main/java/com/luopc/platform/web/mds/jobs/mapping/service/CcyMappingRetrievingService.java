package com.luopc.platform.web.mds.jobs.mapping.service;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.luopc.platform.web.mds.config.EconomicsApiConfig;
import com.luopc.platform.web.mds.jobs.mapping.dto.CurrencyMapping;
import jakarta.annotation.Resource;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author by Robin
 * @className CcyMappingRetrievingService
 * @description TODO
 * @date 2024/1/6 0006 10:52
 */
@Slf4j
@Setter
@Service
@NoArgsConstructor
public class CcyMappingRetrievingService {

    @Resource
    private EconomicsApiConfig economicsApiConfig;


    private List<CurrencyMapping> filterInvalidMapping(List<CurrencyMapping> currencyMappingList) {
        return Optional.ofNullable(currencyMappingList).orElse(Lists.newArrayList()).stream().filter(
                currencyMapping -> (StringUtils.isNoneBlank(currencyMapping.getCcy()) && StringUtils.isNumeric(currencyMapping.getCcyNum()))).collect(Collectors.toList());
    }

    public List<CurrencyMapping> getCurrencyMappingFromIban() {
        String url = this.economicsApiConfig.getIbanMappingUrl();

        try {
            Connection con = Jsoup.connect(url);
            Document document = con.get();
            Elements tables = document.body().getElementsByClass("table-bordered");
            if (CollectionUtil.isNotEmpty(tables)) {
                Element pTable = tables.get(0);
                Elements trs = (pTable.getElementsByTag("tbody").get(0)).children();
                return filterInvalidMapping(trs.stream().filter((tr) -> !tr.children().isEmpty())
                        .map(this::convertToCurrencyMappingForIban)
                        .collect(Collectors.toList()));
            }

            log.error("Cannot get Currency Mapping From Iban, url = {}, text = {}", url, document.body().text());
        } catch (IOException var7) {
            log.error("Unable to retrieve data from API, url = {}", url, var7);
        }

        return null;
    }

    public List<CurrencyMapping> getCurrencyMappingFromLocal() {
        String sourceFile = "static/CurrencyMappingTable.html";
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(sourceFile)) {
            Document document = Jsoup.parse(inputStream, "UTF-8", "");
            return filterInvalidMapping(this.getCurrencyMappingFromDocument(document, "Local"));
        } catch (IOException var3) {
            log.error("Unable to find the source file, path = {}", sourceFile, var3);
            return null;
        }
    }

    public List<CurrencyMapping> getCurrencyMappingFromCnHuiLv() {
        String url = this.economicsApiConfig.getCurrencyMappingUrl();

        try {
            Connection con = Jsoup.connect(url);
            Document document = con.get();
            return filterInvalidMapping(this.getCurrencyMappingFromDocument(document, "HuiLv"));
        } catch (IOException var4) {
            log.error("Unable to retrieve data from API, url = {}", url, var4);
            return null;
        }
    }

    private List<CurrencyMapping> getCurrencyMappingFromDocument(Document document, String source) {
        Elements tables = document.body().getElementsByClass("table-responsive");
        if (CollectionUtil.isNotEmpty(tables)) {
            Element pTable = tables.get(0);
            Elements trs = (pTable.getElementsByTag("tbody").get(0)).children();
            return trs.stream().filter((tr) -> !tr.children().isEmpty())
                    .map(tr -> convertToCurrencyMappingForHuiLv(tr, source))
                    .collect(Collectors.toList());
        } else {
            log.error("Cannot get Currency Mapping From CnHuiLv, text = {}", document.body().text());
            return null;
        }
    }

    private CurrencyMapping convertToCurrencyMappingForIban(Element tr) {
        CurrencyMapping currencyMapping = new CurrencyMapping();
        Element countryName = tr.getElementsByTag("td").get(0);
        Element currencyName = tr.getElementsByTag("td").get(1);
        Element ccyCode = tr.getElementsByTag("td").get(2);
        Element ccyNum = tr.getElementsByTag("td").get(3);
        currencyMapping.setCcy(ccyCode.text());
        currencyMapping.setCcyNum(ccyNum.text());
        currencyMapping.setCurrencyName(currencyName.text());
        currencyMapping.setCountryName(countryName.text());
        currencyMapping.setSourcePlatform("Iban");
        return currencyMapping;
    }

    private CurrencyMapping convertToCurrencyMappingForHuiLv(Element tr, String source) {
        CurrencyMapping currencyMapping = new CurrencyMapping();
        Element countryName = tr.getElementsByTag("td").get(3);
        Element currencyName = tr.getElementsByTag("td").get(2);
        Element ccyCode = tr.getElementsByTag("td").get(0);
        Element ccyNum = tr.getElementsByTag("td").get(1);
        currencyMapping.setCcy(ccyCode.text());
        currencyMapping.setCcyNum(ccyNum.text());
        currencyMapping.setCurrencyName(currencyName.text());
        currencyMapping.setCountryName(countryName.text());
        currencyMapping.setSourcePlatform(source);
        return currencyMapping;
    }
}
