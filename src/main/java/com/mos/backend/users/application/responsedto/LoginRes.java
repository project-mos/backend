package com.mos.backend.users.application.responsedto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRes {
    private String accessToken;
    private String refreshToken;

    public static LoginRes createRes(String accessToken, String refreshToken) {
        return new LoginRes(accessToken, refreshToken);
    }
}
