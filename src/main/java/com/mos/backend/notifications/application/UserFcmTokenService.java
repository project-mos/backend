package com.mos.backend.notifications.application;

import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.notifications.entity.UserFcmToken;
import com.mos.backend.notifications.infrastructure.userfcmtoken.UserFcmTokenRepository;
import com.mos.backend.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}
