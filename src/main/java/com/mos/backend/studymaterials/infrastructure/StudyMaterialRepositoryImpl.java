package com.mos.backend.studymaterials.infrastructure;

import com.mos.backend.studies.entity.Study;
import com.mos.backend.studymaterials.entity.StudyMaterial;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StudyMaterialRepositoryImpl implements StudyMaterialRepository{

    private final StudyMaterialJpaRepository studyMaterialJpaRepository;

    @Override
    public void save(StudyMaterial studyMaterial) {
        studyMaterialJpaRepository.save(studyMaterial);
    }

    @Override
    public void deleteByFilePath(String filePath) {
        studyMaterialJpaRepository.deleteByFilePath(filePath);
    }

    @Override
    public Optional<Long> sumTotalFileSizeByStudy(Study study) {
        return studyMaterialJpaRepository.sumTotalFileSizeByStudy(study);
    }

    @Override
    public Optional<StudyMaterial> findById(Long studyMaterialId) {
        return studyMaterialJpaRepository.findById(studyMaterialId);
    }

    @Override
    public void delete(StudyMaterial studyMaterial) {
        studyMaterialJpaRepository.delete(studyMaterial);
    }

    @Override
    public List<StudyMaterial> findByStudy(Study study) {
        return studyMaterialJpaRepository.findByStudy(study);
    }

    @Override
    public void deleteAllByStudy(Study study) {
        studyMaterialJpaRepository.deleteAllByStudy(study);
    }

}
