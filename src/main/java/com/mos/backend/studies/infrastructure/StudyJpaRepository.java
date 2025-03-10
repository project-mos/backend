package com.mos.backend.studies.infrastructure;

import com.mos.backend.studies.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudyJpaRepository extends JpaRepository<Study, Long> {
    @Query("update Study s set s.viewCount = s.viewCount + 1 where s.id = :studyId")
    @Modifying
    void increaseViewCount(@Param("studyId") long studyId);
}
