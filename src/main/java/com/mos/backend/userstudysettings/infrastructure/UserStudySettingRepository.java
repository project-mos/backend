package com.mos.backend.userstudysettings.infrastructure;

import com.mos.backend.studymembers.entity.StudyMember;
import com.mos.backend.userstudysettings.entity.UserStudySetting;

import java.util.Optional;

public interface UserStudySettingRepository {
    Optional<UserStudySetting> findByStudyMember(StudyMember studyMember);

    void save(UserStudySetting userStudySetting);
}
