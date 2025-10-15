package com.mos.backend.notifications.application.dto.payload;

import com.mos.backend.studyjoins.entity.StudyJoinStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudyJoinResultPayload extends DataPayload{
    private Long studyId;
    private Long userId;
    private StudyJoinStatus status;

    public static StudyJoinResultPayload approved(Long studyId, Long userId) {
        StudyJoinResultPayload payload = new StudyJoinResultPayload();
        payload.studyId = studyId;
        payload.userId = userId;
        payload.status = StudyJoinStatus.APPROVED;
        return payload;
    }

    public static StudyJoinResultPayload rejected(Long studyId, Long userId) {
        StudyJoinResultPayload payload = new StudyJoinResultPayload();
        payload.studyId = studyId;
        payload.userId = userId;
        payload.status = StudyJoinStatus.REJECTED;
        return payload;
    }
}
