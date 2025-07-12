package com.mos.backend.studies.infrastructure;

import com.mos.backend.common.utils.QueryDslSortUtil;
import com.mos.backend.studies.application.responsedto.StudiesResponseDto;
import com.mos.backend.studies.entity.*;
import com.mos.backend.studymembers.entity.ParticipationStatus;
import com.mos.backend.studymembers.entity.QStudyMember;
import com.mos.backend.users.application.responsedto.UserStudiesResponseDto;
import com.mos.backend.users.entity.User;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

import static com.mos.backend.studies.entity.QStudy.study;
import static com.mos.backend.studymembers.entity.QStudyMember.studyMember;
import static com.mos.backend.userstudylikes.entity.QUserStudyLike.userStudyLike;
import static com.querydsl.core.util.StringUtils.isNullOrEmpty;

@Repository
@RequiredArgsConstructor
public class StudyQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Page<StudiesResponseDto> findStudies(Long currentUserId, Pageable pageable, String categoryCond, String meetingTypeCond, String recruitmentStatusCond, String progressStatusCond, boolean liked) {
        List<Long> findStudyId = jpaQueryFactory
                .select(study.id)
                .from(study)
                .where(
                        categoryEq(categoryCond),
                        meetingTypeEq(meetingTypeCond),
                        recruitmentStatusEq(recruitmentStatusCond),
                        progressStatusEq(progressStatusCond),
                        likedStudy(currentUserId, liked)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifiers(pageable))
                .fetch();

        List<StudiesResponseDto> results = jpaQueryFactory
                .select(Projections.constructor(StudiesResponseDto.class,
                        study.id,
                        study.title,
                        study.category,
                        study.meetingType,
                        study.progressStatus,
                        study.recruitmentStatus,
                        study.content,
                        study.recruitmentStartDate,
                        study.recruitmentEndDate,
                        JPAExpressions
                                .select(studyMember.count())
                                .from(studyMember)
                                .where(studyMember.study.eq(study)
                                        .and(studyMember.status.in(Arrays.asList(ParticipationStatus.ACTIVATED, ParticipationStatus.COMPLETED)))
                                ),
                        study.maxStudyMemberCount,
                        study.viewCount,
                        study.tags))
                .from(study)
                .where(study.id.in(findStudyId))
                .orderBy(getOrderSpecifiers(pageable))
                .fetch();

        JPQLQuery<Study> countQuery = jpaQueryFactory
                .select(study)
                .from(study)
                .where(
                        categoryEq(categoryCond),
                        meetingTypeEq(meetingTypeCond),
                        recruitmentStatusEq(recruitmentStatusCond),
                        progressStatusEq(progressStatusCond)
                );
        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchCount);
    }

    public List<UserStudiesResponseDto> readUserStudies(User user, String progressStatusCond, String participationStatusCond) {
        return jpaQueryFactory
                .select(Projections.constructor(UserStudiesResponseDto.class,
                        study.id,
                        study.title,
                        study.category,
                        study.meetingType,
                        study.progressStatus,
                        QStudyMember.studyMember.status,
                        JPAExpressions
                                .select(studyMember.count())
                                .from(studyMember)
                                .where(studyMember.study.eq(study)
                                        .and(studyMember.status.in(ParticipationStatus.ACTIVATED, ParticipationStatus.COMPLETED))
                                ),
                        study.maxStudyMemberCount,
                        study.schedule,
                        study.tags,
                        QStudyMember.studyMember.roleType
                ))

                .from(QStudyMember.studyMember)
                .join(QStudyMember.studyMember.study, study)
                .where(
                        QStudyMember.studyMember.user.eq(user),
                        participationStatusEq(participationStatusCond),
                        progressStatusEq(progressStatusCond)
                )
                .orderBy(studyMember.createdAt.desc())
                .fetch();
    }


    private BooleanExpression categoryEq(String categoryCond) {
        return !isNullOrEmpty(categoryCond) ? study.category.eq(Category.fromDescription(categoryCond)) : null;
    }

    private BooleanExpression meetingTypeEq(String meetingTypeCond) {
        return !isNullOrEmpty(meetingTypeCond) ? study.meetingType.eq(MeetingType.fromDescription(meetingTypeCond)) : null;
    }

    private BooleanExpression recruitmentStatusEq(String recruitmentStatusCond) {
        return !isNullOrEmpty(recruitmentStatusCond) ? study.recruitmentStatus.eq(RecruitmentStatus.fromDescription(recruitmentStatusCond)) : null;
    }

    private BooleanExpression progressStatusEq(String progressStatusCond) {
        return !isNullOrEmpty(progressStatusCond) ? study.progressStatus.eq(ProgressStatus.fromDescription(progressStatusCond)) : null;
    }

    private BooleanExpression participationStatusEq(String participationStatusCond) {
        return !isNullOrEmpty(participationStatusCond) ? studyMember.status.eq(ParticipationStatus.fromDescription(participationStatusCond)) : null;
    }

    private static OrderSpecifier[] getOrderSpecifiers(Pageable pageable) {
        return QueryDslSortUtil.toOrderSpecifiers(pageable.getSort(), Study.class).toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression likedStudy(Long currentUserId, boolean liked) {
        if (!liked || currentUserId == null) {
            return null;
        }

        return study.id.in(
                JPAExpressions
                        .select(userStudyLike.study.id)
                        .from(userStudyLike)
                        .where(userStudyLike.user.id.eq(currentUserId))
        );
    }
}
