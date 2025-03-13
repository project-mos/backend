package com.mos.backend.studies.entity;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.studies.entity.exception.StudyErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Category {
    PROGRAMMING("프로그래밍"), BOOK("독서"), LANGUAGE("어학"), CERTIFICATION("자격증"), HOBBY("취미"), EXAM("고시/공무원");

    private final String description;

    public static Category fromDescription(String description) {
        return Arrays.stream(Category.values())
                .filter(c -> c.description.equals(description))
                .findFirst().orElseThrow(()-> new MosException(StudyErrorCode.CATEGORY_NOT_FOUND));
    }
}
