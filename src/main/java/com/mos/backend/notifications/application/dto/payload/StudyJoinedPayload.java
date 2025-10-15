package com.mos.backend.notifications.application.dto.payload;

import com.mos.backend.studyjoins.entity.StudyJoinStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudyJoinedPayload extends DataPayload{
    private Long studyId;
    private Long studyJoinId;
    private StudyJoinStatus status;

    public static StudyJoinedPayload requested(Long studyId, Long studyJoinId) {
        StudyJoinedPayload studyJoinedPayload = new StudyJoinedPayload();
        studyJoinedPayload.studyId = studyId;
        studyJoinedPayload.studyJoinId = studyJoinId;
        studyJoinedPayload.status = StudyJoinStatus.PENDING;
        return studyJoinedPayload;
    }

    public static StudyJoinedPayload canceled(Long studyId, Long studyJoinId) {
        StudyJoinedPayload studyJoinedPayload = new StudyJoinedPayload();
        studyJoinedPayload.studyId = studyId;
        studyJoinedPayload.studyJoinId = studyJoinId;
        studyJoinedPayload.status = StudyJoinStatus.CANCELED;
        return studyJoinedPayload;
    }
}


