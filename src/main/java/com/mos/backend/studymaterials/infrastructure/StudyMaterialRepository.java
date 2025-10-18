package com.mos.backend.studymaterials.infrastructure;

import com.mos.backend.studies.entity.Study;
import com.mos.backend.studymaterials.entity.StudyMaterial;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudyMaterialRepository {
    void save(StudyMaterial studyMaterial);

    void deleteByFilePath(String filePath);

    Optional<Long> sumTotalFileSizeByStudy(@Param("study") Study study);

    Optional<StudyMaterial> findById(Long studyMaterialId);

    void delete(StudyMaterial studyMaterial);

    List<StudyMaterial> findByStudy(Study study);

    void deleteAllByStudy(Study study);
}
