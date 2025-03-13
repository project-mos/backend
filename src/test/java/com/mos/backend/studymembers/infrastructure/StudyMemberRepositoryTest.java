package com.mos.backend.studymembers.infrastructure;

import com.mos.backend.studies.entity.Category;
import com.mos.backend.studies.entity.MeetingType;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studies.entity.StudyTag;
import com.mos.backend.studies.infrastructure.StudyRepository;
import com.mos.backend.studymembers.entity.ParticipationStatus;
import com.mos.backend.studymembers.entity.StudyMember;
import com.mos.backend.testconfig.AbstractTestContainer;
import com.mos.backend.users.entity.OauthProvider;
import com.mos.backend.users.entity.User;
import com.mos.backend.users.entity.UserRole;
import com.mos.backend.users.infrastructure.respository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StudyMemberRepositoryTest extends AbstractTestContainer {

    @Autowired
    private StudyMemberRepositoryImpl studyMemberRepository;
    @Autowired
    private StudyMemberJpaRepository studyMemberJpaRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Study에 현재 참여 중인 수 조회 성공 테스트")
    void countByStudyAndStatusInTest() {
        // given
        Study study = createStudy();
        Study savedStudy = studyRepository.save(study);

        User user1 = createUser("user1");
        User user2 = createUser("user2");
        User user3 = createUser("user3");
        User user4 = createUser("user4");
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);

        StudyMember studyMember1 = StudyMember.create(savedStudy, user1);
        StudyMember studyMember2 = StudyMember.create(savedStudy, user2);
        StudyMember studyMember3 = StudyMember.create(savedStudy, user3);
        studyMember3.withDrawStudy();
        StudyMember studyMember4 = StudyMember.create(savedStudy, user4);
        studyMember4.completeStudy();
        studyMemberJpaRepository.saveAll(Arrays.asList(studyMember1, studyMember2, studyMember3, studyMember4));

        // when
        int count = studyMemberRepository.countByStudyAndStatusIn(savedStudy, Arrays.asList(ParticipationStatus.COMPLETED, ParticipationStatus.ACTIVATED));

        // then
        assertThat(count).isEqualTo(3);
    }

    private Study createStudy() {
        return Study.builder()
                .title("Test Study")
                .content("Test Content")
                .maxStudyMemberCount(5)
                .category(Category.PROGRAMMING)
                .schedule("Test Schedule")
                .recruitmentStartDate(now())
                .recruitmentEndDate(now())
                .viewCount(0)
                .color("red")
                .meetingType(MeetingType.OFFLINE)
                .tags(StudyTag.fromList(Arrays.asList("tag1", "tag2")))
                .requirements("None")
                .build();
    }

    private User createUser(String name) {
        return User.builder()
                .nickname(name)
                .oauthProvider(OauthProvider.KAKAO)
                .role(UserRole.USER)
                .socialId("1")
                .build();
    }
}