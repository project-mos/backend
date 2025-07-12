package com.mos.backend.studies.infrastructure;

import com.mos.backend.studies.application.responsedto.StudiesResponseDto;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.users.application.responsedto.UserStudiesResponseDto;
import com.mos.backend.users.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface StudyRepository {
    Study save(Study study);
    Optional<Study> findById(Long id);

    void increaseViewCount(long studyId);

    Page<StudiesResponseDto> findStudies(Long currentUserId, Pageable pageable, String categoryCond, String meetingTypeCond, String recruitmentStatusCond, String progressStatusCond, boolean liked);

    long count();

    void delete(Study study);

    List<UserStudiesResponseDto> readUserStudies(User user, String progressStatusCond, String participationStatusCond);
}
