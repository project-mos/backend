package com.mos.backend.userstudysettings.entity;

import com.mos.backend.studymembers.entity.StudyMember;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "user_study_settings")
@Getter
public class UserStudySetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_member_id", nullable = false)
    private StudyMember studyMember;

    private boolean noticePined = true;

    private boolean notificationEnabled = true;

    public void hideNotice() {
        noticePined = false;
    }

    public static UserStudySetting create(StudyMember studyMember) {
        UserStudySetting setting = new UserStudySetting();
        setting.studyMember = studyMember;
        return setting;
    }

    public void update(boolean isNoticePined, boolean isNotificationEnabled) {
        noticePined = isNoticePined;
        notificationEnabled = isNotificationEnabled;
    }

}
