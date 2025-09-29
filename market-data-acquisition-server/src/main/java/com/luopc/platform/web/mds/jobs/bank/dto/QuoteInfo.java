package com.luopc.platform.web.mds.jobs.bank.dto;

import lombok.Data;

@Data
public class QuoteInfo {

    private String curno;
    private String curnm;
    private BankQuoteInfo BOC;
    private BankQuoteInfo CCB;
    private BankQuoteInfo ICBC;
    private BankQuoteInfo ABC;
    private BankQuoteInfo CEB;
}
