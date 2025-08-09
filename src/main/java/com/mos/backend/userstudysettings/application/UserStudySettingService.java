package com.mos.backend.userstudysettings.application;

import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.studymembers.entity.StudyMember;
import com.mos.backend.userstudysettings.application.responsedto.UserStudySettingResponseDto;
import com.mos.backend.userstudysettings.entity.UserStudySetting;
import com.mos.backend.userstudysettings.infrastructure.UserStudySettingRepository;
import com.mos.backend.userstudysettings.presentation.requestdto.UpdateUserStudySettingRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserStudySettingService {
    private final UserStudySettingRepository userStudySettingRepository;
    private final EntityFacade entityFacade;

    @Transactional(readOnly = true)
    @PreAuthorize("@studySecurity.isMemberOrAdmin(#studyId)")
    public UserStudySettingResponseDto read(Long studyId, Long userId) {
        UserStudySetting userStudySetting = entityFacade.getUserStudySetting(userId, studyId);
        return UserStudySettingResponseDto.from(userStudySetting);
    }

    /**
     * 스터디 상단 고정 공지 안보이게 하기
     */
    @PreAuthorize("@studySecurity.isMemberOrAdmin(#studyId)")
    public UserStudySettingResponseDto hideNotice(Long studyId, Long userId) {
        UserStudySetting userStudySetting = entityFacade.getUserStudySetting(userId, studyId);
        userStudySetting.hideNotice();
        return UserStudySettingResponseDto.from(userStudySetting);
    }

    /**
     * userStudySetting 전체 업데이트
     */
    @PreAuthorize("@studySecurity.isMemberOrAdmin(#studyId)")
    public UserStudySettingResponseDto update(UpdateUserStudySettingRequestDto requestDto, Long studyId, Long userId) {
        UserStudySetting userStudySetting = entityFacade.getUserStudySetting(userId, studyId);
        userStudySetting.update(requestDto.getNoticePined(), requestDto.getNotificationEnabled());
        return UserStudySettingResponseDto.from(userStudySetting);
    }

    /**
     * userStudySetting 생성
     */
    public void create(Long studyId, Long userId) {
        StudyMember studyMember = entityFacade.getStudyMember(userId, studyId);
        userStudySettingRepository.save(UserStudySetting.create(studyMember));
    }
}
