package com.mos.backend.userstudysettings.infrastructure;

import com.mos.backend.studymembers.entity.StudyMember;
import com.mos.backend.userstudysettings.entity.UserStudySetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserStudySettingJpaRepository extends JpaRepository<UserStudySetting, Long> {

    Optional<UserStudySetting> findByStudyMember(StudyMember studyMember);

}
