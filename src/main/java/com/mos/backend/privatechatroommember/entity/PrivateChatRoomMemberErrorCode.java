package com.mos.backend.privatechatroommember.entity;

import com.mos.backend.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.util.Locale;

@RequiredArgsConstructor
public enum PrivateChatRoomMemberErrorCode implements ErrorCode {
    CONFLICT(HttpStatus.NOT_FOUND, "private-chat-room-member.conflict"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "private-chat-room-member.not-found");

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
