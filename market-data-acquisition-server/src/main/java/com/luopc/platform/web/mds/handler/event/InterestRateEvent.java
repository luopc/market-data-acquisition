package com.luopc.platform.web.mds.handler.event;

import com.luopc.platform.web.mds.rates.domain.entity.CcyInterestDO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * @author Robin
 */
@Getter
@Setter
public class InterestRateEvent extends ApplicationEvent {

    private static final long serialVersionUID = -2753705718295396310L;

    private List<CcyInterestDO> changeList;

    public InterestRateEvent(Object source, List<CcyInterestDO> changeList) {
        super(source);
        this.changeList = changeList;
    }
}
