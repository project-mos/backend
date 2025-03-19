package com.mos.backend.studycurriculum.presentation.requestdto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StudyCurriculumCreateRequestDto {
    private Long id;
    @NotNull(message = "sectionId 필수입니다.")
    @Min(value = 1, message = "sectionId는 1보다 커야합니다.")
    private Long sectionId;
    @NotBlank(message = "title 필수입니다.")
    private String title;
    private String content;

}
