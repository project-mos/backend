package com.mos.backend.notifications.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mos.backend.common.event.EventType;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataPayloadDto {
    private String type;

    // STUDY 관련
    private String studyId;
    private String studyName;

    // FILE 관련
    private String fileName;

    public static DataPayloadDto forFileUpload(EventType type, Long studyId, String studyName, String fileName) {
        DataPayloadDto dataPayloadDto = new DataPayloadDto();
        dataPayloadDto.type = type.toString();
        dataPayloadDto.studyId = studyId.toString();
        dataPayloadDto.studyName = studyName;
        dataPayloadDto.fileName = fileName;
        return dataPayloadDto;
    }
}
