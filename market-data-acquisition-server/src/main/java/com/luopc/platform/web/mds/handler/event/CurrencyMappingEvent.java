package com.luopc.platform.web.mds.handler.event;

import com.luopc.platform.web.mds.jobs.mapping.dto.CurrencyMapping;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * @author Robin
 */
@Getter
@Setter
public class CurrencyMappingEvent extends ApplicationEvent {

    private static final long serialVersionUID = -2753705718295396328L;

    private List<CurrencyMapping> changeList;

    public CurrencyMappingEvent(Object source, List<CurrencyMapping> changeList) {
        super(source);
        this.changeList = changeList;
    }

}
