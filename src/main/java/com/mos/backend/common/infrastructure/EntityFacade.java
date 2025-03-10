package com.mos.backend.common.infrastructure;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.users.entity.User;
import com.mos.backend.users.entity.exception.UserErrorCode;
import com.mos.backend.users.infrastructure.respository.UserRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class EntityFacade {
    private final UserRepositoryImpl userRepository;

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new MosException(UserErrorCode.USER_NOT_FOUND));
    }
}
