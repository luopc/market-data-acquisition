package com.luopc.platform.web.mds.convertors;

import com.luopc.platform.web.mds.jobs.mapping.dto.CurrencyMapping;
import com.luopc.platform.web.mds.jobs.rates.dto.interest.InterestRate;
import com.luopc.platform.web.mds.rates.domain.dto.BankQuotation;
import com.luopc.platform.web.mds.rates.domain.entity.BankQuotationDO;
import com.luopc.platform.web.mds.rates.domain.entity.CcyInterestDO;
import com.luopc.platform.web.mds.rates.domain.entity.CurrencyMappingDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Robin
 */
@Mapper(componentModel = "spring")
public interface MapStructConvertor {

    CurrencyMappingDO currencyMappingToDO(CurrencyMapping currencyMapping);

    @Mapping(source = "updateTime", target = "updatedTime")
    BankQuotationDO bankQuotationToDO(BankQuotation bankQuotation);

    @Mapping(source = "updatedTime", target = "updateTime")
    BankQuotation doToBankQuotation(BankQuotationDO bankQuotation);

    @Mapping(source = "updateDate", target = "updatedTime", dateFormat = "yyyy-MM")
    @Mapping(source = "realRate", target = "rate", dateFormat = "yyyy-MM")
    @Mapping(source = "realPreRate", target = "preRate", dateFormat = "yyyy-MM")
    CcyInterestDO interestRateToDO(InterestRate interestRate);


}
