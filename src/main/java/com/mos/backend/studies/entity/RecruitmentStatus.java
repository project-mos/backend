package com.mos.backend.studies.entity;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.studies.entity.exception.StudyErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum RecruitmentStatus {
    OPEN("모집 중"),
    CLOSED("모집 완료");

    private final String description;

    public static RecruitmentStatus fromDescription(String description) {
        return Arrays.stream(RecruitmentStatus.values())
                .filter(r -> r.description.equals(description))
                .findFirst().orElseThrow(()-> new MosException(StudyErrorCode.INVALID_RECRUITMENT_STATUS));
    }
}
