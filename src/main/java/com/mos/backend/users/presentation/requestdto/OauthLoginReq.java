package com.mos.backend.users.presentation.requestdto;

import com.mos.backend.users.entity.OauthProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OauthLoginReq {
    private String code;
    private OauthProvider oauthProvider;
}
