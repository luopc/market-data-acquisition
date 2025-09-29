package com.luopc.platform.web.mds.handler.event;

import com.luopc.platform.market.api.SpotRate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * @author Robin
 */
@Getter
@Setter
public class SpotRateEvent extends ApplicationEvent {

    private static final long serialVersionUID = -2753705718295396310L;

    private List<SpotRate> changeList;

    public SpotRateEvent(Object source, List<SpotRate> changeList) {
        super(source);
        this.changeList = changeList;
    }
}
