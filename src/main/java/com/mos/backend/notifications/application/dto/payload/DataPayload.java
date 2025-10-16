package com.mos.backend.notifications.application.dto.payload;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = StudyFileUploadPayload.class, name = "FILE_UPLOAD"),
        @JsonSubTypes.Type(value = StudyJoinedPayload.class, name = "STUDY_JOIN_REQUEST"),
        @JsonSubTypes.Type(value = StudyJoinResultPayload.class, name = "STUDY_JOIN_RESULT"),
        @JsonSubTypes.Type(value = NewStudyMemberPayload.class, name = "NEW_STUDY_MEMBER"),
        @JsonSubTypes.Type(value = StudyDeletedPayload.class, name = "STUDY_DELETED"),
})
public abstract class DataPayload {
}
