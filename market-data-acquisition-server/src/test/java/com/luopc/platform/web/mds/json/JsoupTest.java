package com.luopc.platform.web.mds.json;

import cn.hutool.core.io.file.FileReader;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Slf4j
public class JsoupTest {


    @Test
    public void getCurrencyMapping() throws IOException {
        //获取连接
        FileReader reader = new FileReader("static/CurrencyMappingTable.html");
        Document document = Jsoup.parse(reader.getFile());
        //通过class属性 ，获取子类元素
        Element pTable = document.body().getElementsByClass("table-responsive").get(0);
        Elements trs = pTable.getElementsByTag("tbody").get(0).children();

        //遍历<tr>标签
        trs.forEach(tr -> {

            if (!tr.children().isEmpty()) {
                Element country = tr.getElementsByTag("td").get(0);
                Element interest = tr.getElementsByTag("td").get(1);
                Element preInterest = tr.getElementsByTag("td").get(2);
                Element updateTime = tr.getElementsByTag("td").get(3);

//                log.debug("country={}, interest={}, preInterest={}, updateTime={}", country.text(), interest.text(), preInterest.text(), updateTime.text());
            }

        });
    }


    @Test
    public void getInterest() throws IOException {
        String testURL = "https://zh.tradingeconomics.com/country-list/interest-rate";  //目标网页
        //获取连接
        Connection con = Jsoup.connect(testURL);
        //填写参数
        con.data("continent", "europe");

        //选择发送方式,获取整个网页信息，存在documnet类
        Document document = con.get();
        //通过class属性 ，获取子类元素
        Element pTable = document.body().getElementsByClass("table-responsive").get(0);
        Elements trs = pTable.getElementsByTag("tbody").get(0).children();

        //遍历<tr>标签
        trs.forEach(tr -> {

            if (!tr.children().isEmpty()) {
                Element country = tr.getElementsByTag("td").get(0);
                Element interest = tr.getElementsByTag("td").get(1);
                Element preInterest = tr.getElementsByTag("td").get(2);
                Element updateTime = tr.getElementsByTag("td").get(3);
                Element unit = tr.getElementsByTag("td").get(4);
//                log.debug("country={}, interest={}, preInterest={}, updateTime={}, unit={}", country.text(), interest.text(), preInterest.text(), updateTime.text(), unit.text());
            }

        });
    }


    //    @Test
    public void getCurrency() throws IOException {
        String testURL = "http://www.cnhuilv.com/currency/";  //目标网页
        //获取连接
        Connection con = Jsoup.connect(testURL);

        //选择发送方式,获取整个网页信息，存在documnet类
        Document document = con.get();
        log.debug(document.body().text());
        //通过class属性 ，获取子类元素
        Element pTable = document.body().getElementsByClass("table-responsive").get(0);
        Elements trs = pTable.getElementsByTag("tbody").get(0).children();

        //遍历<tr>标签
        trs.forEach(tr -> {

            if (!tr.children().isEmpty()) {
                Element country = tr.getElementsByTag("td").get(0);
                Element interest = tr.getElementsByTag("td").get(1);
                Element preInterest = tr.getElementsByTag("td").get(2);
                Element updateTime = tr.getElementsByTag("td").get(3);

//                log.debug("country={}, interest={}, preInterest={}, updateTime={}, updateTime={}", country.text(), interest.text(), preInterest.text(), updateTime.text(), updateTime.text());
            }

        });
    }

    @Test
    public void getBankCode() throws IOException {
        String testURL = "http://www.cnhuilv.com/bank/cgbchina/";  //目标网页
        //获取连接
        Connection con = Jsoup.connect(testURL);

        //选择发送方式,获取整个网页信息，存在documnet类
        Document document = con.get();
        //通过class属性 ，获取子类元素
        Elements unitDiv = document.body().getElementsByClass("hlinfoflags");

        //遍历<tr>标签
        unitDiv.forEach(div -> {

//           log.debug("div: {}", div.text());

        });
    }


    @Test
    public void getCurrencyCode() throws IOException {
        String testURL = "https://www.iban.hk/currency-codes";  //目标网页
        //获取连接
        Connection con = Jsoup.connect(testURL);

        //选择发送方式,获取整个网页信息，存在documnet类
        Document document = con.get();
        //通过class属性 ，获取子类元素
        Element pTable = document.body().getElementsByClass("table-bordered").get(0);
        Elements trs = pTable.getElementsByTag("tbody").get(0).children();

        //遍历<tr>标签
        trs.forEach(tr -> {

            if (!tr.children().isEmpty()) {
                Element country = tr.getElementsByTag("td").get(0);
                Element interest = tr.getElementsByTag("td").get(1);
                Element preInterest = tr.getElementsByTag("td").get(2);
                Element updateTime = tr.getElementsByTag("td").get(3);
//                log.debug("country={}, interest={}, preInterest={}, updateTime={}, updateTime={}", country.text(), interest.text(), preInterest.text(), updateTime.text(), updateTime.text());
            }

        });
    }

}
