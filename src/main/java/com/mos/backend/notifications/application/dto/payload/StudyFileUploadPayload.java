package com.mos.backend.notifications.application.dto.payload;

import com.mos.backend.studymaterials.entity.FileUploadStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudyFileUploadPayload extends DataPayload{
    private Long studyId;
    private FileUploadStatus status;

    // 성공
    public static StudyFileUploadPayload success(Long studyId) {
        StudyFileUploadPayload payload = new StudyFileUploadPayload();
        payload.studyId = studyId;
        payload.status = FileUploadStatus.SUCCESS;
        return payload;
    }

    // 실패
    public static StudyFileUploadPayload failure(Long studyId) {
        StudyFileUploadPayload payload = new StudyFileUploadPayload();
        payload.studyId = studyId;
        payload.status = FileUploadStatus.FAILURE;
        return payload;
    }
}
