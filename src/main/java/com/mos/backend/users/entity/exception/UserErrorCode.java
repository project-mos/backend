package com.mos.backend.users.entity.exception;

import com.mos.backend.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.util.Locale;

@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "auth.user.not-found"),
    USER_FORBIDDEN(HttpStatus.FORBIDDEN, "auth.user.forbidden"),
    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "auth.user.unauthorized"),
    MISSING_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "auth.user.missing-access-token"),;

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
