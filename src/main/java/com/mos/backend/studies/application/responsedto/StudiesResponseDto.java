package com.mos.backend.studies.application.responsedto;

import com.mos.backend.studies.entity.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class StudiesResponseDto {
    private Long id;
    private String title;
    private String category;
    private String meetingType;
    private String progressStatus;
    private String recruitmentStatus;
    private LocalDate recruitmentEndDate;
    private Long currentStudyMembers;
    private Integer maxStudyMembers;
    private Integer viewCount;
    private List<String> tags;

    public StudiesResponseDto(Long studyId, String title, Category category, MeetingType meetingType, ProgressStatus progressStatus,
                              RecruitmentStatus recruitmentStatus, LocalDate recruitmentEndDate, Long currentStudyMembers, Integer maxStudyMembers, Integer viewCount, StudyTag tags) {
        this.id = studyId;
        this.title = title;
        this.category = category.getDescription();
        this.meetingType = meetingType.getDescription();
        this.progressStatus = progressStatus.getDescription();
        this.recruitmentStatus = recruitmentStatus.getDescription();
        this.recruitmentEndDate = recruitmentEndDate;
        this.currentStudyMembers = currentStudyMembers;
        this.maxStudyMembers = maxStudyMembers;
        this.viewCount = viewCount;
        this.tags = tags.toList();
    }

    public static StudiesResponseDto from(Study study, Long currentStudyMembers) {
        StudiesResponseDto studiesResponseDto = new StudiesResponseDto();
        studiesResponseDto.id = study.getId();
        studiesResponseDto.title = study.getTitle();
        studiesResponseDto.category = study.getCategory().getDescription();
        studiesResponseDto.meetingType = study.getMeetingType().getDescription();
        studiesResponseDto.progressStatus = study.getProgressStatus().getDescription();
        studiesResponseDto.recruitmentStatus = study.getRecruitmentStatus().getDescription();
        studiesResponseDto.recruitmentEndDate = study.getRecruitmentEndDate();
        studiesResponseDto.currentStudyMembers = currentStudyMembers;
        studiesResponseDto.maxStudyMembers = study.getMaxStudyMemberCount();
        studiesResponseDto.viewCount = study.getViewCount();
        studiesResponseDto.tags = study.getTags().toList();
        return studiesResponseDto;
    }

}
