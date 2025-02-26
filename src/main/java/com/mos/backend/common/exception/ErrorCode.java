package com.mos.backend.common.exception;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

public interface ErrorCode {
    HttpStatus getStatus();
    String getErrorName();
    String getMessage(MessageSource messageSource);
}
