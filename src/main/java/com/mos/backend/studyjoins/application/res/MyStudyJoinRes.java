package com.mos.backend.studyjoins.application.res;

import com.mos.backend.studies.entity.Study;
import com.mos.backend.studyjoins.entity.StudyJoin;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MyStudyJoinRes {
    private Long studyId;
    private String title;
    private String category;

    private Long studyJoinId;
    private String studyJoinStatus;
    private LocalDateTime createdAt;

    public static MyStudyJoinRes from(StudyJoin studyJoin) {
        Study study = studyJoin.getStudy();

        MyStudyJoinRes myStudyJoinRes = new MyStudyJoinRes();
        myStudyJoinRes.studyId = study.getId();
        myStudyJoinRes.title = study.getTitle();
        myStudyJoinRes.category = study.getCategory().getDescription();

        myStudyJoinRes.studyJoinId = studyJoin.getId();
        myStudyJoinRes.studyJoinStatus = studyJoin.getStatus().getDescription();
        myStudyJoinRes.createdAt = studyJoin.getCreatedAt();

        return myStudyJoinRes;
    }
}
