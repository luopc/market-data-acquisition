package com.luopc.platform.web.mds.handler.event;

import com.luopc.platform.web.mds.rates.domain.dto.BankQuotation;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;
import java.util.List;

/**
 * @author Robin
 */
@Getter
@Setter
public class BankQuotationEvent extends ApplicationEvent implements Serializable {

    private static final long serialVersionUID = -2753705718295396390L;

    private List<BankQuotation> changeList;

    public BankQuotationEvent(Object source, List<BankQuotation> changeList) {
        super(source);
        this.changeList = changeList;
    }
}
