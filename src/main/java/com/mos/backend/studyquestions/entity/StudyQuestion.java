package com.mos.backend.studyquestions.entity;

import com.mos.backend.common.entity.BaseAuditableEntity;
import com.mos.backend.studies.entity.Study;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "study_questions")
public class StudyQuestion extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_question_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @Column(nullable = false)
    private Long questionNum;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private QuestionType type;

    @Embedded
    private QuestionOption options;

    @Column(nullable = false)
    private boolean required;

    public static StudyQuestion create(Study study, Long questionId, String question, QuestionType type, QuestionOption options, boolean required) {
        StudyQuestion studyQuestion = new StudyQuestion();
        studyQuestion.study = study;
        studyQuestion.questionNum = questionId;
        studyQuestion.question = question;
        studyQuestion.type = type;
        studyQuestion.options = options;
        studyQuestion.required = required;
        return studyQuestion;
    }
}
