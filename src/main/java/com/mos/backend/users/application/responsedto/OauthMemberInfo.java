package com.mos.backend.users.application.responsedto;


import com.mos.backend.users.entity.OauthProvider;

public interface OauthMemberInfo {
    String getSocialId();

    String getEmail();

    String getNickname();

    OauthProvider getOauthProvider();
}
