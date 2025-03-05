package com.mos.backend.users.infrastructure.respository;

import com.mos.backend.users.entity.OauthProvider;
import com.mos.backend.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByOauthProviderAndSocialId(OauthProvider oauthProvider, String socialId);
}
