package com.mos.backend.studycurriculum.entity;

import com.mos.backend.common.entity.BaseAuditableEntity;
import com.mos.backend.studies.entity.Study;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "study_curriculums")
public class StudyCurriculum extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_curriculum_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Long sectionId;

    @Lob
    @Column(nullable = true)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    public static StudyCurriculum create(Study study, String title, Long sectionId, String content) {
        StudyCurriculum studyCurriculum = new StudyCurriculum();
        studyCurriculum.study = study;
        studyCurriculum.title = title;
        studyCurriculum.sectionId = sectionId;
        studyCurriculum.content = content;
        return studyCurriculum;
    }

    public void update(String title, Long sectionId, String content) {
        this.title = title;
        this.sectionId = sectionId;
        this.content = content;
    }
}
