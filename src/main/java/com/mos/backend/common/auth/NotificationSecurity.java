package com.mos.backend.common.auth;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.users.entity.User;
import com.mos.backend.users.entity.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("notificationSecurity")
@RequiredArgsConstructor
public class NotificationSecurity {
    private final EntityFacade entityFacade;

    public boolean isRecipientOrAdmin(Long notificationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        isAuthenticated(authentication);

        Long currentUserId = Long.valueOf(authentication.getName());
        User currentUser = entityFacade.getUser(currentUserId);

        if (currentUser.isAdmin()) {
            return true;
        }

        return currentUserId.equals(entityFacade.getNotificationLog(notificationId).getRecipient().getId());
    }


    private static void isAuthenticated(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new MosException(UserErrorCode.USER_UNAUTHORIZED);
        }
    }
}
