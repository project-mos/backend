package com.mos.backend.notifications.infrastructure.userfcmtoken;

import com.mos.backend.notifications.entity.UserFcmToken;
import com.mos.backend.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserFcmTokenJpaRepository extends JpaRepository<UserFcmToken, Long> {
    UserFcmToken user(User user);

    void deleteByUserAndToken(User user, String token);

    List<UserFcmToken> findByUserId(Long userId);
}
