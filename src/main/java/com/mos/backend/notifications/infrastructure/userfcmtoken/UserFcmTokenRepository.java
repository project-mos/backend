package com.mos.backend.notifications.infrastructure.userfcmtoken;

import com.mos.backend.notifications.entity.UserFcmToken;
import com.mos.backend.users.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserFcmTokenRepository {

    void save(UserFcmToken userFcmToken);

    void deleteByUserAndToken(User user, String token);

    List<UserFcmToken> findByUserId(Long userId);

    List<UserFcmToken> findByUserIdsWithUser(List<Long> userIds);
}
