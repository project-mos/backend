package com.mos.backend.userstudysettings.application;

import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studymembers.entity.StudyMember;
import com.mos.backend.users.entity.User;
import com.mos.backend.userstudysettings.application.responsedto.UserStudySettingResponseDto;
import com.mos.backend.userstudysettings.entity.UserStudySetting;
import com.mos.backend.userstudysettings.infrastructure.UserStudySettingRepository;
import com.mos.backend.userstudysettings.presentation.requestdto.UpdateUserStudySettingRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserStudySettingServiceTest {

    @InjectMocks
    private UserStudySettingService userStudySettingService;

    @Mock
    private UserStudySettingRepository userStudySettingRepository;

    @Mock
    private EntityFacade entityFacade;

    @Mock
    private StudyMember mockStudyMember;

    @Mock
    private User mockUser;

    @Mock
    private Study mockStudy;

    private static final Long USER_ID = 1L;
    private static final Long STUDY_ID = 100L;

    private void setupDtoConversionStubs() {
        given(mockStudyMember.getUser()).willReturn(mockUser);
        given(mockUser.getId()).willReturn(USER_ID);
        given(mockStudyMember.getStudy()).willReturn(mockStudy);
        given(mockStudy.getId()).willReturn(STUDY_ID);
    }

    @Test
    @DisplayName("스터디 설정 조회에 성공한다")
    void read_userStudySetting() {
        // given
        setupDtoConversionStubs();
        UserStudySetting userStudySetting = UserStudySetting.create(mockStudyMember);
        given(entityFacade.getUserStudySetting(USER_ID, STUDY_ID)).willReturn(userStudySetting);

        // when
        UserStudySettingResponseDto responseDto = userStudySettingService.read(STUDY_ID, USER_ID);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.isNoticePined()).isTrue();
        assertThat(responseDto.isNotificationEnabled()).isTrue();
        assertThat(responseDto.getUserId()).isEqualTo(USER_ID);

        verify(entityFacade).getUserStudySetting(USER_ID, STUDY_ID);
    }

    @Test
    @DisplayName("공지사항 숨기기 처리에 성공한다")
    void hideNotice_successfully() {
        // given
        setupDtoConversionStubs();
        UserStudySetting userStudySetting = UserStudySetting.create(mockStudyMember);
        given(entityFacade.getUserStudySetting(USER_ID, STUDY_ID)).willReturn(userStudySetting);

        // when
        UserStudySettingResponseDto responseDto = userStudySettingService.hideNotice(STUDY_ID, USER_ID);

        // then
        assertThat(userStudySetting.isNoticePined()).isFalse();
        assertThat(responseDto.isNoticePined()).isFalse();
        assertThat(responseDto.getUserId()).isEqualTo(USER_ID);

        verify(entityFacade).getUserStudySetting(USER_ID, STUDY_ID);
    }

    @Test
    @DisplayName("스터디 설정 업데이트에 성공한다")
    void update_userStudySetting() {
        // given
        setupDtoConversionStubs();
        UserStudySetting userStudySetting = UserStudySetting.create(mockStudyMember);
        UpdateUserStudySettingRequestDto requestDto = new UpdateUserStudySettingRequestDto(false, false);
        given(entityFacade.getUserStudySetting(USER_ID, STUDY_ID)).willReturn(userStudySetting);

        // when
        UserStudySettingResponseDto responseDto = userStudySettingService.update(requestDto, STUDY_ID, USER_ID);

        // then
        assertThat(userStudySetting.isNoticePined()).isFalse();
        assertThat(userStudySetting.isNotificationEnabled()).isFalse();
        assertThat(responseDto.isNoticePined()).isFalse();
        assertThat(responseDto.isNotificationEnabled()).isFalse();
        assertThat(responseDto.getUserId()).isEqualTo(USER_ID);

        verify(entityFacade).getUserStudySetting(USER_ID, STUDY_ID);
    }

    @Test
    @DisplayName("스터디 설정 생성에 성공한다")
    void create_userStudySetting() {
        // given
        given(entityFacade.getStudyMember(USER_ID, STUDY_ID)).willReturn(mockStudyMember);

        // when
        userStudySettingService.create(STUDY_ID, USER_ID);

        // then
        ArgumentCaptor<UserStudySetting> captor = ArgumentCaptor.forClass(UserStudySetting.class);
        verify(userStudySettingRepository).save(captor.capture());

        UserStudySetting capturedSetting = captor.getValue();
        assertThat(capturedSetting.getStudyMember()).isEqualTo(mockStudyMember);
        assertThat(capturedSetting.isNoticePined()).isTrue();
        assertThat(capturedSetting.isNotificationEnabled()).isTrue();
    }
}