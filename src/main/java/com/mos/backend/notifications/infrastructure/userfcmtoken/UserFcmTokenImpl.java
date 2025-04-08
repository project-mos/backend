package com.mos.backend.notifications.infrastructure.userfcmtoken;

import com.mos.backend.notifications.entity.UserFcmToken;
import com.mos.backend.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserFcmTokenImpl implements UserFcmTokenRepository{

    private final UserFcmTokenJpaRepository userFcmTokenJpaRepository;

    @Override
    public void save(UserFcmToken userFcmToken) {
        userFcmTokenJpaRepository.save(userFcmToken);
    }

    @Override
    public void deleteByUserAndToken(User user, String token) {
        userFcmTokenJpaRepository.deleteByUserAndToken(user, token);
    }

    @Override
    public List<UserFcmToken> findByUserId(Long userId) {
        return userFcmTokenJpaRepository.findByUserId(userId);
    }
}
