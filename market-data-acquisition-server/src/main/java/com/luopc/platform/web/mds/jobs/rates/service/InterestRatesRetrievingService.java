package com.luopc.platform.web.mds.jobs.rates.service;

import com.luopc.platform.market.tools.CountryCurrencyUtil;
import com.luopc.platform.web.mds.config.EconomicsApiConfig;
import com.luopc.platform.web.mds.convertors.MapStructConvertor;
import com.luopc.platform.web.mds.jobs.rates.dto.interest.InterestRate;
import com.luopc.platform.market.api.CountryMapCurrency;
import com.luopc.platform.web.mds.rates.domain.entity.CcyInterestDO;
import jakarta.annotation.Resource;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author by Robin
 * @className InterestRatesRetrievingService
 * @description 各个国家的利率数据
 * @date 2024/1/6 0006 10:57
 */
@Slf4j
@Setter
@Service
@NoArgsConstructor
public class InterestRatesRetrievingService {

    @Resource
    private EconomicsApiConfig economicsApiConfig;
    @Resource
    private MapStructConvertor mapStructConvertor;

    public List<CcyInterestDO> getInterestRates() {
        String continents = economicsApiConfig.getInterestContinents();
        String url = economicsApiConfig.getInterestRateUrl();

        List<CcyInterestDO> interestRateList = new ArrayList<>();
        if (StringUtils.hasLength(continents)) {
            Arrays.stream(continents.split(",")).forEach(continent -> {
                String finalUrl = url + continent;
                List<InterestRate> interestRates = getInterestRates(finalUrl);
                if (CollectionUtils.isNotEmpty(interestRates)) {
                    interestRateList.addAll(interestRates.stream().map(mapStructConvertor::interestRateToDO).filter(Objects::nonNull).collect(Collectors.toList()));
                }
            });
        }
        return interestRateList;
    }


    private List<InterestRate> getInterestRates(String url) {
        try {
            //获取连接
            Connection con = Jsoup.connect(url);
            //选择发送方式,获取整个网页信息，存在documnet类
            Document document = con.get();
            //通过class属性 ，获取子类元素
            Element pTable = document.body().getElementsByClass("table-responsive").get(0);
            Elements trs = pTable.getElementsByTag("tbody").get(0).children();

            //遍历<tr>标签
            return trs.stream().map(tr -> {

                if (!tr.children().isEmpty()) {
                    InterestRate interestRate = new InterestRate();

                    Element country = tr.getElementsByTag("td").get(0);
                    Element interest = tr.getElementsByTag("td").get(1);
                    Element preInterest = tr.getElementsByTag("td").get(2);
                    Element updateTime = tr.getElementsByTag("td").get(3);
                    Element unit = tr.getElementsByTag("td").get(4);

                    String countryName = convertCountryName(country.text());

                    interestRate.setCountryName(countryName);
                    interestRate.setRate(Double.parseDouble(interest.text()));
                    interestRate.setPreRate(Double.parseDouble(preInterest.text()));
                    interestRate.setUpdateDate(updateTime.text());
                    interestRate.setUnit(unit.text());

                    String ccyCode = CountryCurrencyUtil.getCurrencyByCountry(countryName);
                    if (Objects.nonNull(ccyCode)) {
                        log.debug("Get ccyCode {} from Cache for {}", ccyCode, countryName);
                        interestRate.setCcy(ccyCode);
                        return interestRate;
                    } else {
                        log.warn("CcyCode cannot be found for Country, {}", interestRate);
                    }

                }
                return null;

            }).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Unable to retrieve data from API, url = {}", url, e);
        }
        return null;
    }

    private String convertCountryName(String countryName) {
        if ("印尼".equals(countryName)) {
            return "印度尼西亚";
        } else if ("巴哈马".equals(countryName)) {
            return "巴哈马元";
        } else if ("欧元区".equals(countryName)) {
            return "欧盟";
        } else if ("刚果共和国".equals(countryName)) {
            return "刚果";
        } else if ("南苏丹".equals(countryName)) {
            return "苏丹";
        } else if ("多明尼加共和国".equals(countryName)) {
            return "多米尼加";
        } else if ("特里尼达和多巴哥".equals(countryName)) {
            return "特立尼达多巴哥";
        } else if ("象牙海岸".equals(countryName)) {
            return "非洲金融共同体";
        }
        return countryName;
    }
}
