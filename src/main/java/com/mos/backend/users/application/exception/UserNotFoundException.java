package com.mos.backend.users.application.exception;

import com.mos.backend.common.exception.ErrorCode;
import com.mos.backend.common.exception.MosException;
import com.mos.backend.users.entity.exception.UserErrorCode;

public class UserNotFoundException extends MosException {
    public UserNotFoundException() {
        super(UserErrorCode.USER_NOT_FOUND_EXCEPTION);
    }
}
