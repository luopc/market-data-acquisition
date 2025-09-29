package com.luopc.platform.web.mds.handler.event;

import com.luopc.platform.market.api.MarketPrices;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * @author Robin
 */
@Getter
@Setter
public class MarketPricesEvent extends ApplicationEvent {

    private static final long serialVersionUID = -2753705718295396329L;

    private List<MarketPrices> changeList;

    public MarketPricesEvent(Object source, List<MarketPrices> changeList) {
        super(source);
        this.changeList = changeList;
    }
}
