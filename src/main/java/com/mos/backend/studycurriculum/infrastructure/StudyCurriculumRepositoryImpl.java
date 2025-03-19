package com.mos.backend.studycurriculum.infrastructure;

import com.mos.backend.studies.entity.Study;
import com.mos.backend.studycurriculum.entity.StudyCurriculum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StudyCurriculumRepositoryImpl implements StudyCurriculumRepository {

    private final StudyCurriculumJpaRepository studyCurriculumJpaRepository;

    @Override
    public void saveAll(List<StudyCurriculum> studyCurriculumList) {
        studyCurriculumJpaRepository.saveAll(studyCurriculumList);
    }

    @Override
    public List<StudyCurriculum> findAllByStudy(Study study) {
        return studyCurriculumJpaRepository.findAllByStudy(study);
    }

    @Override
    public void deleteAll(List<StudyCurriculum> studyCurriculumList) {
        studyCurriculumJpaRepository.deleteAll(studyCurriculumList);
    }

    @Override
    public void save(StudyCurriculum studyCurriculum) {
        studyCurriculumJpaRepository.save(studyCurriculum);
    }

    @Override
    public Optional<StudyCurriculum> findByIdAndStudy(Long id, Study study) {
        return studyCurriculumJpaRepository.findByIdAndStudy(id, study);
    }
}
