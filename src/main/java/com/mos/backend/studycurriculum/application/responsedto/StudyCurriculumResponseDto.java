package com.mos.backend.studycurriculum.application.responsedto;

import com.mos.backend.studycurriculum.entity.StudyCurriculum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudyCurriculumResponseDto {
    private Long id;
    private String title;
    private String content;
    private Long sectionId;

    public static StudyCurriculumResponseDto from(StudyCurriculum studyCurriculum) {
        StudyCurriculumResponseDto responseDto = new StudyCurriculumResponseDto();
        responseDto.id = studyCurriculum.getId();
        responseDto.title = studyCurriculum.getTitle();
        responseDto.content = studyCurriculum.getContent();
        responseDto.sectionId = studyCurriculum.getSectionId();
        return responseDto;
    }
}
