package com.mos.backend.users.application.client;


import com.mos.backend.users.application.param.OauthParams;
import com.mos.backend.users.application.responsedto.OauthMemberInfo;
import com.mos.backend.users.entity.OauthProvider;

public interface OauthClient {
    OauthProvider oauthProvider();

    String getOauthLoginToken(OauthParams oauthParams);

    OauthMemberInfo getMemberInfo(String accessToken);

}
