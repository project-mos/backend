package com.mos.backend.users.infrastructure.respository;

import com.mos.backend.users.entity.OauthProvider;
import com.mos.backend.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;
    private final UserRedisRepository userRedisRepository;

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public Optional<User> findByOauthProviderAndSocialId(OauthProvider oauthProvider, String socialId) {
        return userJpaRepository.findByOauthProviderAndSocialId(oauthProvider, socialId);
    }
}
