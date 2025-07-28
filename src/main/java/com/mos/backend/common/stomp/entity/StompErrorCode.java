package com.mos.backend.common.stomp.entity;

import com.mos.backend.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.util.Locale;

@RequiredArgsConstructor
public enum StompErrorCode implements ErrorCode {
    MISSING_USER_ID_IN_HEADER(HttpStatus.BAD_REQUEST, "stomp.missing-user-id-in-header"),
    INVALID_DESTINATION(HttpStatus.BAD_REQUEST, "stomp.invalid-destination"),
    MISSING_USER_ID_IN_SESSION(HttpStatus.BAD_REQUEST, "stomp.missing-user-id-in-session"),;


    private final HttpStatus httpStatus;
    private final String messageKey;

    @Override
    public HttpStatus getStatus() {
        return httpStatus;
    }

    @Override
    public String getErrorName() {
        return this.name();
    }

    @Override
    public String getMessage(MessageSource messageSource) {
        return messageSource.getMessage(messageKey, null, Locale.getDefault());
    }
}
