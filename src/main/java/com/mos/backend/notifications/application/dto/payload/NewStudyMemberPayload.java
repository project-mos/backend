package com.mos.backend.notifications.application.dto.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NewStudyMemberPayload extends DataPayload{
    private Long studyId;
}
