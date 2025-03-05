package com.mos.backend.studies.presentation.dto;

import com.mos.backend.studies.entity.MeetingType;
import com.mos.backend.studycurriculum.presentation.requestdto.StudyCurriculumCreateRequestDto;
import com.mos.backend.studyquestions.presentation.requestdto.StudyQuestionCreateRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;


import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class StudyCreateRequestDto {

  @NotBlank(message = "title 필수입니다.")
  private String title;
  @NotBlank(message = "category 필수입니다.")
  private String category;
  @NotNull(message = "maxParticipants 필수입니다.")
  @Min(value = 1, message = "maxParticipants는 1보다 커야합니다.")
  private Integer maxParticipants;
  @NotNull(message = "recruitmentStartDate 필수입니다.")
  private LocalDate recruitmentStartDate;
  @NotNull(message = "recruitmentEndDate 필수입니다.")
  private LocalDate recruitmentEndDate;
  @NotNull(message = "tags 필수입니다.")
  private List<String> tags;
  @NotNull(message = "meetingType 필수입니다.")
  private String meetingType;
  private String schedule;
  @NotBlank(message = "content 필수입니다.")
  private String content;
  private String requirements;

  @Valid
  @NotNull(message = "curriculums 필수입니다.")
  private List<StudyCurriculumCreateRequestDto> curriculums;
  private List<String> rules;
  private List<String> benefits;
  @Valid
  @NotNull
  private List<StudyQuestionCreateRequestDto> applicationQuestions;

}
