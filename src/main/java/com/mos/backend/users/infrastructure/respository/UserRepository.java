package com.mos.backend.users.infrastructure.respository;

import com.mos.backend.users.entity.OauthProvider;
import com.mos.backend.users.entity.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> findByOauthProviderAndSocialId(OauthProvider oauthProvider, String socialId);
}
