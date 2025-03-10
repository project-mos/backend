package com.mos.backend.users.application;

import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.studies.entity.Category;
import com.mos.backend.users.application.responsedto.UserDetailRes;
import com.mos.backend.users.entity.User;
import com.mos.backend.users.presentation.requestdto.UserUpdateReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class UserService {
    private final EntityFacade entityFacade;

    public void update(Long userId, UserUpdateReq req) {
        User user = entityFacade.getUser(userId);

        String categories = req.getCategories().stream()
                .map(Category::getDescription)
                .collect(Collectors.joining(","));

        user.update(req.getNickname(), req.getIntroduction(), categories);
    }

    public UserDetailRes getDetail(Long userId) {
        User user = entityFacade.getUser(userId);
        return UserDetailRes.from(user);
    }
}
