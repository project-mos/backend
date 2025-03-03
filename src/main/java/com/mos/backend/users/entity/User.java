package com.mos.backend.users.entity;

import com.mos.backend.common.entity.BaseTimeEntity;
import com.mos.backend.users.application.responsedto.OauthMemberInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OauthProvider oauthProvider;

    @Column
    private String socialId;

    @Column
    private String introduction;

    public User(OauthMemberInfo oauthMemberInfo) {
        this.role = UserRole.USER;
        this.socialId = oauthMemberInfo.getSocialId();
        this.nickname = oauthMemberInfo.getNickname();
        this.oauthProvider = oauthMemberInfo.getOauthProvider();
    }

}
