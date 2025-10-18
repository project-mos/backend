package com.mos.backend.studymaterials.infrastructure;

import com.mos.backend.studies.entity.Study;
import com.mos.backend.studymaterials.entity.StudyMaterial;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import reactor.util.annotation.NonNull;

import java.util.List;
import java.util.Optional;

public interface StudyMaterialJpaRepository extends JpaRepository<StudyMaterial, Long> {

    void deleteByFilePath(String filePath);

    @Query("SELECT SUM(sm.fileSize) FROM StudyMaterial sm WHERE sm.study = :study")
    Optional<Long> sumTotalFileSizeByStudy(Study study);

    @EntityGraph(attributePaths = {"studyMember", "studyMember.user"})
    List<StudyMaterial> findByStudy(Study study);

    @EntityGraph(attributePaths = {"studyMember", "studyMember.user"})
    @NonNull
    Optional<StudyMaterial> findById(@NonNull Long studyMaterialId);

    void deleteAllByStudy(Study study);
}
