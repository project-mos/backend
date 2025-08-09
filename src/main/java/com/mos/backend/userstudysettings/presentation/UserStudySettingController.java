package com.mos.backend.userstudysettings.presentation;

import com.mos.backend.userstudysettings.application.UserStudySettingService;
import com.mos.backend.userstudysettings.application.responsedto.UserStudySettingResponseDto;
import com.mos.backend.userstudysettings.presentation.requestdto.UpdateUserStudySettingRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserStudySettingController {
    private final UserStudySettingService userStudySettingService;

    @GetMapping("/studies/{studyId}/settings")
    @ResponseStatus(HttpStatus.OK)
    public UserStudySettingResponseDto read(
            @PathVariable Long studyId,
            @AuthenticationPrincipal Long currentUserId
    ) {
        return userStudySettingService.read(studyId, currentUserId);
    }

    /**
     * 스터디룸 상단 고정 공지 안보이게 하기
     */
    @PutMapping("/studies/{studyId}/settings/hide-notice")
    @ResponseStatus(HttpStatus.OK)
    public UserStudySettingResponseDto hideNotice(
            @PathVariable Long studyId,
            @AuthenticationPrincipal Long currentUserId
    ) {
        return userStudySettingService.hideNotice(studyId, currentUserId);
    }

    /**
     * 스터디 관련 설정 전체 업데이트
     */
    @PutMapping("/studies/{studyId}/settings")
    @ResponseStatus(HttpStatus.OK)
    public UserStudySettingResponseDto update(
            @PathVariable Long studyId,
            @Valid @RequestBody UpdateUserStudySettingRequestDto requestDto,
            @AuthenticationPrincipal Long currentUserId
    ) {
        return userStudySettingService.update(requestDto, studyId, currentUserId);
    }
}
