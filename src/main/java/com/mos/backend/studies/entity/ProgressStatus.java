package com.mos.backend.studies.entity;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.studies.entity.exception.StudyErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ProgressStatus {

    NOT_STARTED("진행 전"),
    IN_PROGRESS("진행 중"),
    COMPLETED("완료"),
    PAUSED("정지"),
    FAILED("실패");

    private final String description;

    public static ProgressStatus fromDescription(String description) {
        return Arrays.stream(ProgressStatus.values())
                .filter(p -> p.description.equals(description))
                .findFirst().orElseThrow(()-> new MosException(StudyErrorCode.INVALID_PROGRESS_STATUS));
    }
}
