package com.mos.backend.studyjoins.application.res;

import com.mos.backend.studyjoins.entity.StudyJoin;
import com.mos.backend.users.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class StudyJoinRes {
    private Long studyJoinId;
    private String studyJoinStatus;
    private LocalDateTime createdAt;

    private Long userId;
    private String nickname;

    private List<QuestionAnswerRes> questionAnswerResList;

    public static StudyJoinRes of(StudyJoin sj, List<QuestionAnswerRes> questionAnswers) {
        User user = sj.getUser();
        StudyJoinRes studyJoinRes = new StudyJoinRes();
        studyJoinRes.studyJoinId = sj.getId();
        studyJoinRes.studyJoinStatus = sj.getStatus().getDescription();
        studyJoinRes.createdAt = sj.getCreatedAt();
        studyJoinRes.userId = user.getId();
        studyJoinRes.nickname = user.getNickname();
        studyJoinRes.questionAnswerResList = questionAnswers;
        return studyJoinRes;
    }
}
