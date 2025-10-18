package com.mos.backend.notifications.application.dto.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewStudyMemberPayload extends DataPayload{
    private Long studyId;
}
