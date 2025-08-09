package com.mos.backend.userstudysettings.presentation.requestdto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateUserStudySettingRequestDto {
    @NotNull(message = "noticePined는 필수입니다.")
    private Boolean noticePined;
    @NotNull(message = "notificationEnabled는 필수입니다.")
    private Boolean notificationEnabled;
}
