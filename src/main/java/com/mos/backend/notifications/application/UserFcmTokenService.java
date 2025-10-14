package com.mos.backend.notifications.application;

import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.notifications.entity.UserFcmToken;
import com.mos.backend.notifications.infrastructure.userfcmtoken.UserFcmTokenRepository;
import com.mos.backend.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserFcmTokenService {

    private final UserFcmTokenRepository userFcmTokenRepository;
    private final EntityFacade entityFacade;

    public void create(Long userId, String token) {
        User user = entityFacade.getUser(userId);
        userFcmTokenRepository.save(UserFcmToken.create(user, token));
    }

    public void delete(Long userId, String token) {
        User user = entityFacade.getUser(userId);
        userFcmTokenRepository.deleteByUserAndToken(user, token);
    }

    @Transactional(readOnly = true)
    public List<UserFcmToken> findByUserId(Long userId) {
        return userFcmTokenRepository.findByUserId(userId);
    }

    /**
     * 여러 사용자 ID에 해당하는 모든 FCM 토큰을 조회하여 "토큰-유저ID" 맵으로 반환
     */
    @Transactional(readOnly = true)
    public Map<String, Long> findTokensByUserIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<UserFcmToken> tokens = userFcmTokenRepository.findByUserIdsWithUser(userIds);

        return tokens.stream()
                .collect(Collectors.toMap(
                        UserFcmToken::getToken,
                        token -> token.getUser().getId(),
                        (existing, replacement) -> existing
                ));
    }
}
