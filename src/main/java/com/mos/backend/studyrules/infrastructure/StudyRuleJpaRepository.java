package com.mos.backend.studyrules.infrastructure;

import com.mos.backend.studyrules.entity.StudyRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRuleJpaRepository extends JpaRepository<StudyRule, Long> {
}