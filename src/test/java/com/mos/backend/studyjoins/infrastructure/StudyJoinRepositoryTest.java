package com.mos.backend.studyjoins.infrastructure;

import com.mos.backend.studies.entity.Category;
import com.mos.backend.studies.entity.MeetingType;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studies.infrastructure.StudyRepository;
import com.mos.backend.studyjoins.entity.StudyJoin;
import com.mos.backend.studyjoins.entity.StudyJoinStatus;
import com.mos.backend.testconfig.AbstractTestContainer;
import com.mos.backend.users.entity.OauthProvider;
import com.mos.backend.users.entity.User;
import com.mos.backend.users.entity.UserRole;
import com.mos.backend.users.infrastructure.respository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StudyJoinRepositoryTest extends AbstractTestContainer {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudyRepository studyRepository;
    @Autowired
    private StudyJoinRepository studyJoinRepository;
    @Autowired
    private StudyJoinJpaRepository studyJoinJpaRepository;

    @Test
    @DisplayName("특정 상태의 신청 목록 조회 쿼리 테스트")
    @DirtiesContext
    void findAllByUserIdAndStatusTest() {
        // Given
        User user1 = saveUser();
        User user2 = saveUser();
        User user3 = saveUser();

        Study study1 = saveStudy();
        Study study2 = saveStudy();
        Study study3 = saveStudy();

        StudyJoin studyJoin1 = saveStudyJoin(user1, study1, StudyJoinStatus.PENDING);
        StudyJoin studyJoin2 = saveStudyJoin(user1, study1, StudyJoinStatus.PENDING);
        StudyJoin studyJoin3 = saveStudyJoin(user1, study1, StudyJoinStatus.APPROVED);

        // When
        List<StudyJoin> studyJoins = studyJoinRepository.findAllByUserIdAndStatus(user1.getId(), StudyJoinStatus.PENDING.getDescription());

        // Then
        assertThat(studyJoins).hasSize(2);
        studyJoins.stream().map(StudyJoin::getId).toList().containsAll(List.of(studyJoin1.getId(), studyJoin2.getId()));
    }

    private User saveUser() {
        return userRepository.save(
                User.builder()
                        .nickname("nickname")
                        .oauthProvider(OauthProvider.KAKAO)
                        .role(UserRole.USER)
                        .socialId("1")
                        .build()
        );
    }

    private Study saveStudy() {
        return studyRepository.save(
                Study.builder()
                        .title("스터디 제목")
                        .content("스터디 내용")
                        .maxStudyMemberCount(3)
                        .category(Category.PROGRAMMING)
                        .recruitmentStartDate(LocalDate.now().minusDays(1))
                        .recruitmentEndDate(LocalDate.now().plusDays(5))
                        .color("#FFFFFF")
                        .meetingType(MeetingType.ONLINE)
                        .build()
        );
    }

    private StudyJoin saveStudyJoin(User user, Study study, StudyJoinStatus status) {
        return studyJoinJpaRepository.save(
                StudyJoin.builder()
                        .user(user)
                        .study(study)
                        .status(status)
                        .build()
        );
    }

}
