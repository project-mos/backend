package com.mos.backend.studyjoins.infrastructure;

import com.mos.backend.studyjoins.entity.StudyJoin;
import com.mos.backend.studyjoins.entity.StudyJoinStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.mos.backend.studyjoins.entity.QStudyJoin.studyJoin;
import static com.querydsl.core.util.StringUtils.isNullOrEmpty;

@Repository
@RequiredArgsConstructor
public class StudyJoinQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public List<StudyJoin> findAllByUserIdAndStatus(Long userId, String StudyJoinStatusCond) {
        return queryFactory
                .selectFrom(studyJoin)
                .join(studyJoin.study).fetchJoin()
                .where(eqUserId(userId))
                .where(eqStatus(StudyJoinStatusCond))
                .fetch();
    }

    public List<StudyJoin> findAllByStudyIdAndStatus(Long studyId, String StudyJoinStatusCond) {
        return queryFactory
                .selectFrom(studyJoin)
                .join(studyJoin.study).fetchJoin()
                .where(eqStudyId(studyId))
                .where(eqStatus(StudyJoinStatusCond))
                .fetch();
    }

    private static BooleanExpression eqStudyId(Long studyId) {
        return studyJoin.study.id.eq(studyId);
    }

    private static BooleanExpression eqUserId(Long userId) {
        return studyJoin.user.id.eq(userId);
    }

    private static BooleanExpression eqStatus(String StudyJoinStatusCond) {
        return isNullOrEmpty(StudyJoinStatusCond) ? null : studyJoin.status.eq(StudyJoinStatus.fromDescription(StudyJoinStatusCond));
    }
}
