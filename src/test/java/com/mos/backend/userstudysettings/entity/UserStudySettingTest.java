package com.mos.backend.userstudysettings.entity;

import com.mos.backend.studies.entity.Study;
import com.mos.backend.studymembers.entity.StudyMember;
import com.mos.backend.users.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserStudySettingTest {

    @Mock
    private User user;

    @Mock
    private Study study;

    private StudyMember studyMember;

    @BeforeEach
    void setUp() {
        studyMember = StudyMember.createStudyMember(study, user);
    }

    @Test
    @DisplayName("정적 팩토리 메서드로 UserStudySetting 객체를 생성한다")
    void create_userStudySetting() {
        // when
        UserStudySetting setting = UserStudySetting.create(studyMember);

        // then
        assertThat(setting).isNotNull();
        assertThat(setting.getStudyMember()).isEqualTo(studyMember);
        assertThat(setting.isNoticePined()).isTrue();
        assertThat(setting.isNotificationEnabled()).isTrue();
    }

    @Test
    @DisplayName("공지사항 숨기기 기능을 테스트한다")
    void hideNotice() {
        // given
        UserStudySetting setting = UserStudySetting.create(studyMember);

        // when
        setting.hideNotice();

        // then
        assertThat(setting.isNoticePined()).isFalse();
    }

    @Test
    @DisplayName("사용자 스터디 설정을 업데이트한다")
    void update_settings() {
        // given
        UserStudySetting setting = UserStudySetting.create(studyMember);
        boolean newNoticePined = false;
        boolean newNotificationEnabled = false;

        // when
        setting.update(newNoticePined, newNotificationEnabled);

        // then
        assertThat(setting.isNoticePined()).isEqualTo(newNoticePined);
        assertThat(setting.isNotificationEnabled()).isEqualTo(newNotificationEnabled);
    }
}