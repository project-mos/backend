package com.mos.backend.userstudysettings.application.responsedto;

import com.mos.backend.userstudysettings.entity.UserStudySetting;
import lombok.Getter;

@Getter
public class UserStudySettingResponseDto {
    private Long userId;
    private Long studyId;
    private Long studyMemberId;
    private boolean noticePined;
    private boolean notificationEnabled;

    public static UserStudySettingResponseDto from(UserStudySetting userStudySetting) {
        UserStudySettingResponseDto responseDto = new UserStudySettingResponseDto();
        responseDto.userId = userStudySetting.getStudyMember().getUser().getId();
        responseDto.studyId = userStudySetting.getStudyMember().getStudy().getId();
        responseDto.studyMemberId = userStudySetting.getStudyMember().getId();
        responseDto.noticePined = userStudySetting.isNoticePined();
        responseDto.notificationEnabled = userStudySetting.isNotificationEnabled();
        return responseDto;
    }
}
