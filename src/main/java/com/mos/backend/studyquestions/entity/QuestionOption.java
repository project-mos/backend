package com.mos.backend.studyquestions.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Arrays;
import java.util.List;

@Embeddable
public class QuestionOption {

    @Column(name = "options", nullable = true)
    private String value;

    public List<String> toList() {
        if (this.value == null || this.value.isBlank()) {
            return List.of();
        }
        return Arrays.asList(this.value.split(","));
    }

    public static QuestionOption fromList(List<String> options) {
        QuestionOption questionOption = new QuestionOption();
        questionOption.value = String.join(",", options);
        return questionOption;
    }
}
