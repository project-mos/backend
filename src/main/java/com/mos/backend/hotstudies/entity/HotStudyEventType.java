package com.mos.backend.hotstudies.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum HotStudyEventType {
    VIEW(1L),
    LIKE(1L),
    LIKE_CANCEL(-1L),
    JOIN(1L),
    JOIN_CANCEL(-1L),
    DELETE(0L);

    private final Long value;
}
