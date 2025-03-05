package com.mos.backend.users.entity;

import com.mos.backend.users.application.param.GoogleParams;
import com.mos.backend.users.application.param.KakaoParams;
import com.mos.backend.users.application.param.NaverParams;
import com.mos.backend.users.application.param.OauthParams;

public enum OauthProvider {
    KAKAO, GOOGLE, NAVER;

    public OauthParams getOauthParams(String code) {
        return switch (this) {
            case KAKAO -> new KakaoParams(code);
            case GOOGLE -> new GoogleParams(code);
            case NAVER -> new NaverParams(code);
        };
    }
}
