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
    private int currentStudyMemberCount;
    private int maxStudyMemberCount;
    private String category;
    private String schedule;
    private LocalDate recruitmentStartDate;
    private LocalDate recruitmentEndDate;
    private int viewCount;
    private String recruitmentStatus;
    private String progressStatus;
    private String meetingType;
    private List<String> tags;
    private String requirements;

    public static StudyResponseDto from(Study study, int currentStudyMemberCount) {
        StudyResponseDto studyResponseDto = new StudyResponseDto();
        studyResponseDto.id = study.getId();
        studyResponseDto.title = study.getTitle();
        studyResponseDto.content = study.getContent();
        studyResponseDto.currentStudyMemberCount = currentStudyMemberCount;
        studyResponseDto.maxStudyMemberCount = study.getMaxStudyMemberCount();
        studyResponseDto.category = study.getCategory().getDescription();
        studyResponseDto.schedule = study.getSchedule();
        studyResponseDto.recruitmentStartDate = study.getRecruitmentStartDate();
        studyResponseDto.recruitmentEndDate = study.getRecruitmentEndDate();
        studyResponseDto.viewCount = study.getViewCount();
        studyResponseDto.recruitmentStatus = study.getRecruitmentStatus().getDescription();
        studyResponseDto.progressStatus = study.getProgressStatus().getDescription();
        studyResponseDto.meetingType = study.getMeetingType().getDescription();
        studyResponseDto.tags = study.getTags().toList();
        studyResponseDto.requirements = study.getRequirements();
        return studyResponseDto;
    }

}

