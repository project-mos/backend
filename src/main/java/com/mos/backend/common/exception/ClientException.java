package com.mos.backend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ClientException extends RuntimeException{
    private final String message;
    private final HttpStatus status;

    public ClientException(HttpStatus status, String message) {
        super(message);
        this.message = message;
        this.status = status;
    }
}
