package com.mos.backend.common.exception;

import lombok.Getter;

@Getter
public class MosException extends RuntimeException {
    private final ErrorCode errorCode;
    public MosException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
