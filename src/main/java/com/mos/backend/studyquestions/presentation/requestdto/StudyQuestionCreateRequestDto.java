package com.mos.backend.studyquestions.presentation.requestdto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class StudyQuestionCreateRequestDto {

    @NotBlank(message = "question 필수입니다.")
    private String question;
    @NotNull(message = "questionId는 필수입니다.")
    @Min(value = 1, message = "questionId는 1보다 커야합니다.")
    private Long questionNum;
    @NotNull(message = "isRequired 필수입니다.")
    private boolean isRequired;
    @NotBlank(message = "type 필수입니다.")
    private String type;
    @NotNull(message = "options 필수입니다.")
    private List<String> options;
}
