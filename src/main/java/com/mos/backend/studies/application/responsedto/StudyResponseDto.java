package com.mos.backend.studies.application.responsedto;

import com.mos.backend.studies.entity.Study;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class StudyResponseDto {
    private Long id;
    private String title;
    private String content;
    private int maxParticipantsCount;
    private String category;
    private String schedule;
    private LocalDate recruitmentStartDate;
    private LocalDate recruitmentEndDate;
    private int viewCount;
    private String meetingType;
    private List<String> tags;
    private String requirements;

    public static StudyResponseDto from(Study study) {
        StudyResponseDto studyResponseDto = new StudyResponseDto();
        studyResponseDto.id = study.getId();
        studyResponseDto.title = study.getTitle();
        studyResponseDto.content = study.getContent();
        studyResponseDto.maxParticipantsCount = study.getMaxParticipantsCount();
        studyResponseDto.category = study.getCategory().getDescription();
        studyResponseDto.schedule = study.getSchedule();
        studyResponseDto.recruitmentStartDate = study.getRecruitmentStartDate();
        studyResponseDto.recruitmentEndDate = study.getRecruitmentEndDate();
        studyResponseDto.viewCount = study.getViewCount();
        studyResponseDto.meetingType = study.getMeetingType().getDescription();
        studyResponseDto.tags = study.getTags().toList();
        studyResponseDto.requirements = study.getRequirements();
        return studyResponseDto;
    }

}

