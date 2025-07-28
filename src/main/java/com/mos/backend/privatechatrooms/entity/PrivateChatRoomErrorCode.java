package com.mos.backend.privatechatrooms.entity;

import com.mos.backend.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.util.Locale;

@RequiredArgsConstructor
@Getter
public enum PrivateChatRoomErrorCode implements ErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "private-chat-room.not-found"),
    CONFLICT(HttpStatus.CONFLICT, "private-chat-room.conflict"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "private-chat-room.forbidden" ),
    INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "private-chat-room.info-not-found");

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