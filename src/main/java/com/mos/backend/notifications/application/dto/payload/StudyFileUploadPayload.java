package com.mos.backend.notifications.application.dto.payload;

import com.mos.backend.studymaterials.entity.FileUploadStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudyFileUploadPayload extends DataPayload{
    private Long studyId;
    private String studyName;
    private String fileName;
    private FileUploadStatus status;

    // 성공
    public static StudyFileUploadPayload success(Long studyId, String studyName, String fileName) {
        StudyFileUploadPayload payload = new StudyFileUploadPayload();
        payload.studyId = studyId;
        payload.studyName = studyName;
        payload.fileName = fileName;
        payload.status = FileUploadStatus.SUCCESS;
        return payload;
    }

    // 실패
    public static StudyFileUploadPayload failure(Long studyId, String studyName, String fileName) {
        StudyFileUploadPayload payload = new StudyFileUploadPayload();
        payload.studyId = studyId;
        payload.studyName = studyName;
        payload.fileName = fileName;
        payload.status = FileUploadStatus.FAILURE;
        return payload;
    }
}
