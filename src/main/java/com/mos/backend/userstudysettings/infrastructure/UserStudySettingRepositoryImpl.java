package com.mos.backend.userstudysettings.infrastructure;

import com.mos.backend.studymembers.entity.StudyMember;
import com.mos.backend.userstudysettings.entity.UserStudySetting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserStudySettingRepositoryImpl implements UserStudySettingRepository{

    private final UserStudySettingJpaRepository userStudySettingJpaRepository;

    @Override
    public Optional<UserStudySetting> findByStudyMember(StudyMember studyMember) {
        return userStudySettingJpaRepository.findByStudyMember(studyMember);
    }

    @Override
    public void save(UserStudySetting userStudySetting) {
        userStudySettingJpaRepository.save(userStudySetting);
    }
}
