package com.mos.backend.studies.entity.exception;

import com.mos.backend.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.util.Locale;

@RequiredArgsConstructor
public enum StudyErrorCode implements ErrorCode {
    STUDY_NOT_FOUND(HttpStatus.NOT_FOUND, "study.not-found"),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "study.category.not-found"),
    INVALID_MEETING_TYPE(HttpStatus.BAD_REQUEST, "study.meeting-type.invalid"),
    INVALID_RECRUITMENT_DATES(HttpStatus.BAD_REQUEST, "study.invalid-recruitment-dates"),
    INVALID_RECRUITMENT_STATUS(HttpStatus.BAD_REQUEST, "study.invalid-recruitment-status"),
    INVALID_PROGRESS_STATUS(HttpStatus.BAD_REQUEST, "study.invalid-progress-status"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "study.internal-server-error"),
    UNRELATED_STUDY(HttpStatus.BAD_REQUEST, "study.unrelated-study"),
    NOT_IN_RECRUITMENT_PERIOD(HttpStatus.BAD_REQUEST, "study.not-in-recruitment-period"),
    RECRUITMENT_CLOSED(HttpStatus.BAD_REQUEST, "study.recruitment-closed"),
    LOGIN_REQUIRED_FOR_LIKE_FILTER(HttpStatus.UNAUTHORIZED, "study.filter.like.login-required")
    ;


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
