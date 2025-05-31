package com.mos.backend.studyschedules.entity.exception;

import com.mos.backend.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.util.Locale;

@RequiredArgsConstructor
public enum StudyScheduleErrorCode implements ErrorCode {
    STUDY_SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "study-schedule.not-found"),
    STUDY_SCHEDULE_COMPLETED(HttpStatus.BAD_REQUEST, "study-schedule.completed"),
    INVALID_END_DATE_TIME(HttpStatus.BAD_REQUEST, "study-schedule.invalid-end-date-time"),;

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
