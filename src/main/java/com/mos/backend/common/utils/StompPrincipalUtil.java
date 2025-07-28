package com.mos.backend.common.utils;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.users.entity.exception.UserErrorCode;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.security.Principal;

public class StompPrincipalUtil {

    public static Long getUserId(StompHeaderAccessor accessor) {
        return Long.valueOf(accessor.getUser().getName());
    }

    public static void validatePrincipal(StompHeaderAccessor accessor) {
        Principal user = accessor.getUser();
        if (user == null || user.getName() == null) {
            throw new MosException(UserErrorCode.USER_UNAUTHORIZED);
        }
    }
}
