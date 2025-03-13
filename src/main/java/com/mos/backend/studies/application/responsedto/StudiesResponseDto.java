package com.mos.backend.studies.application.responsedto;

import com.mos.backend.studies.entity.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class StudiesResponseDto {
    private Long studyId;
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
        this.studyId = studyId;
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

}
