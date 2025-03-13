package com.mos.backend.studies;

import com.mos.backend.common.utils.RandomColorGenerator;
import com.mos.backend.studies.entity.Category;
import com.mos.backend.studies.entity.MeetingType;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studies.entity.StudyTag;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StudyFixture {

    public static List<Study> createStudiesPerCategoryWithCount(int studyCount) {
        List<Study> studies = new ArrayList<>();
        for (Category category : Category.values()) {
            for (int i = 0; i < studyCount; i++) {
                Study study = Study.builder()
                        .title(category.getDescription() + " 스터디 " + (i + 1))
                        .content(category.getDescription() + " 스터디 내용 " + (i + 1))
                        .maxStudyMemberCount(5 + i)
                        .category(category)
                        .schedule("매주 " + (i % 7 == 0 ? "월" : i % 7 == 1 ? "화" : i % 7 == 2 ? "수" : i % 7 == 3 ? "목" : i % 7 == 4 ? "금" : i % 7 == 5 ? "토" : "일") + "요일")
                        .recruitmentStartDate(LocalDate.now().minusDays(i))
                        .recruitmentEndDate(LocalDate.now().plusDays(7 + i))
                        .viewCount(i * 10)
                        .color(RandomColorGenerator.generateRandomColor())
                        .meetingType(MeetingType.values()[i % MeetingType.values().length]) // 미팅 타입 순환
                        .tags(StudyTag.fromList(Arrays.asList("tag" + i, "tag" + (i + 1))))
                        .requirements("requirement" + i)
                        .build();
                studies.add(study);
            }
        }
        return studies;
    }
}
