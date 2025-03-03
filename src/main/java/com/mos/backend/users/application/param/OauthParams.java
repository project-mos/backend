package com.mos.backend.users.application.param;


import com.mos.backend.users.entity.OauthProvider;
import org.springframework.util.MultiValueMap;

public interface OauthParams {
    OauthProvider oauthProvider();

    String getAuthorizationCode();

    MultiValueMap<String, String> makeBody();
}
