package com.luopc.platform.web.mds.restful.domain;

import com.luopc.platform.common.core.exception.ErrorCode;
import com.luopc.platform.common.core.exception.PlatformErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MDSErrorCode implements ErrorCode {

    RATE_CANNOT_FOUND(PlatformErrorCode.RESOURCE_NO_FOUND, "Rate cannot be found."),
    QUOTE_CANNOT_FOUND(PlatformErrorCode.RESOURCE_NO_FOUND, "Quote cannot be found.");

    private final PlatformErrorCode status;
    private final String msg;

    @Override
    public int getCode() {
        return status.getCode();
    }

    @Override
    public String getMessage() {
        return msg;
    }
}
