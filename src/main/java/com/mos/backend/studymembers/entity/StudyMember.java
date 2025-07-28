package com.mos.backend.studymembers.entity;

import com.mos.backend.common.entity.BaseAuditableEntity;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.users.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;
import static org.hibernate.annotations.OnDeleteAction.CASCADE;

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
    @OnDelete(action = CASCADE)
    private Study study;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ParticipationStatus status = ParticipationStatus.ACTIVATED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudyMemberRoleType roleType;

    private LocalDateTime lastEntryAt = LocalDateTime.now();

    public static StudyMember createStudyLeader(Study study, User user) {
        StudyMember studyMember = new StudyMember();
        studyMember.study = study;
        studyMember.user = user;
        studyMember.status = ParticipationStatus.ACTIVATED;
        studyMember.roleType = StudyMemberRoleType.LEADER;
        return studyMember;
    }

    public static StudyMember createStudyMember(Study study, User user) {
        StudyMember studyMember = new StudyMember();
        studyMember.study = study;
        studyMember.user = user;
        studyMember.status = ParticipationStatus.ACTIVATED;
        studyMember.roleType = StudyMemberRoleType.MEMBER;
        return studyMember;
    }

    public void completeStudy() {
        status = ParticipationStatus.COMPLETED;
    }

    public void withDrawStudy() {
        status = ParticipationStatus.WITHDRAWN;
    }

    public boolean isLeader() {
        return roleType == StudyMemberRoleType.LEADER;
    }

    public void changeToMember() {
        roleType = StudyMemberRoleType.MEMBER;
    }

    public void changeToLeader() {
        roleType = StudyMemberRoleType.LEADER;
    }

    public void updateLastEntryAt() {
        this.lastEntryAt = LocalDateTime.now();
    }
}
