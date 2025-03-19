package com.mos.backend.studycurriculum.entity.exception;

import com.mos.backend.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.util.Locale;

@RequiredArgsConstructor
public enum StudyCurriculumErrorCode implements ErrorCode {
    STUDY_CURRICULUM_NOT_FOUND(HttpStatus.NOT_FOUND, "study-curriculum.not-found"),
    INVALID_SECTION_ID(HttpStatus.BAD_REQUEST, "study-curriculum.invalid-section-id");

    private final HttpStatus httpStatus;
    private final String messageKey;

    @Override
    public HttpStatus getStatus() {
        return httpStatus;
    }

    @Override
    public String getErrorName() {
        return this.name();
    }

    @Override
    public String getMessage(MessageSource messageSource) {
        return messageSource.getMessage(messageKey, null, Locale.getDefault());
    }
    }
