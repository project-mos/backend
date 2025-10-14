package com.mos.backend.notifications.infrastructure.userfcmtoken;

import com.mos.backend.notifications.entity.UserFcmToken;
import com.mos.backend.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserFcmTokenJpaRepository extends JpaRepository<UserFcmToken, Long> {
    void deleteByUserAndToken(User user, String token);

    List<UserFcmToken> findByUserId(Long userId);

    @Query("SELECT t FROM UserFcmToken t LEFT JOIN FETCH t.user WHERE t.user.id IN :userIds")
    List<UserFcmToken> findByUserIdsWithUser(@Param("userIds") List<Long> userIds);
}
