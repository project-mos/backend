package com.mos.backend.users.infrastructure.respository;

import com.mos.backend.users.entity.User;

public interface UserRepository {
    void save(User user);
}
