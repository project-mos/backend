package com.mos.backend.studymembers.entity;

import com.mos.backend.common.entity.BaseAuditableEntity;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.users.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "study_members")
public class StudyMember extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_member_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false, name = "study_id")
    private Study study;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ParticipationStatus status = ParticipationStatus.ACTIVATED;

    public static StudyMember create(Study study, User user) {
        StudyMember studyMember = new StudyMember();
        studyMember.study = study;
        studyMember.user = user;
        studyMember.status = ParticipationStatus.ACTIVATED;
        return studyMember;
    }

    public void completeStudy() {
        status = ParticipationStatus.COMPLETED;
    }

    public void withDrawStudy() {
        status = ParticipationStatus.WITHDRAWN;
    }
}
