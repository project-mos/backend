package com.mos.backend.common.auth;


import com.mos.backend.common.exception.MosException;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.users.entity.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("chatRoomSecurity")
@RequiredArgsConstructor
public class ChatRoomSecurity {
    private final EntityFacade entityFacade;

    public boolean isPrivateChatRoomMember(Long userId, Long privateChatRoomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        isAuthenticated(authentication);

        Long currentUserId = Long.valueOf(authentication.getName());
        if (!currentUserId.equals(userId)) {
            throw new MosException(UserErrorCode.USER_UNAUTHORIZED);
        }

        return entityFacade.getPrivateChatRoomMember(currentUserId, privateChatRoomId) != null;

    }

    private static void isAuthenticated(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new MosException(UserErrorCode.USER_UNAUTHORIZED);
        }
    }
}
