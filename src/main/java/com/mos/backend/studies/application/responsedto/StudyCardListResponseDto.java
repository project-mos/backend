package com.mos.backend.studies.application.responsedto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class StudyCardListResponseDto {
    private long totalStudies;
    private int currentPage;
    private int totalPages;
    private List<StudiesResponseDto> studies;
    
}
