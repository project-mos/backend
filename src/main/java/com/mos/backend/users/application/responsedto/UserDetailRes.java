package com.mos.backend.users.application.responsedto;

import com.mos.backend.users.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDetailRes {
    String nickname;
    String introduction;
    String categories;

    public static UserDetailRes from(User user) {
        return new UserDetailRes(user.getNickname(), user.getIntroduction(), user.getCategories());
    }
}
