package com.mos.backend.privatechatmessages.entity;

import com.mos.backend.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.util.Locale;

@RequiredArgsConstructor
@Getter
public enum PrivateChatMessageErrorCode implements ErrorCode {
    DESERIALIZATION_FAILED(HttpStatus.BAD_REQUEST, "private-chat-message.deserialization-failed"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "private-chat-message.not-found"),;

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