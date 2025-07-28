package com.mos.backend.common;

import com.mos.backend.privatechatmessages.entity.PrivateChatMessage;
import com.mos.backend.privatechatmessages.infrastructure.PrivateChatMessageRepository;
import com.mos.backend.privatechatroommember.entity.PrivateChatRoomMember;
import com.mos.backend.privatechatroommember.infrastructure.PrivateChatRoomMemberRepository;
import com.mos.backend.privatechatrooms.entity.PrivateChatRoom;
import com.mos.backend.privatechatrooms.infrastructure.PrivateChatRoomRepository;
import com.mos.backend.questionanswers.entity.QuestionAnswer;
import com.mos.backend.questionanswers.infrastructure.QuestionAnswerRepository;
import com.mos.backend.studies.entity.Category;
import com.mos.backend.studies.entity.MeetingType;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studies.infrastructure.StudyRepository;
import com.mos.backend.studychatmessages.entity.StudyChatMessage;
import com.mos.backend.studychatmessages.infrastructure.StudyChatMessageRepository;
import com.mos.backend.studychatrooms.entity.StudyChatRoom;
import com.mos.backend.studychatrooms.infrastructure.StudyChatRoomRepository;
import com.mos.backend.studycurriculum.entity.StudyCurriculum;
import com.mos.backend.studycurriculum.infrastructure.StudyCurriculumRepository;
import com.mos.backend.studyjoins.entity.StudyJoin;
import com.mos.backend.studyjoins.entity.StudyJoinStatus;
import com.mos.backend.studyjoins.infrastructure.StudyJoinJpaRepository;
import com.mos.backend.studymembers.entity.StudyMember;
import com.mos.backend.studymembers.infrastructure.StudyMemberRepository;
import com.mos.backend.studyquestions.entity.StudyQuestion;
import com.mos.backend.studyquestions.infrastructure.StudyQuestionRepository;
import com.mos.backend.studyschedulecurriculums.entity.StudyScheduleCurriculum;
import com.mos.backend.studyschedulecurriculums.infrastructure.StudyScheduleCurriculumRepository;
import com.mos.backend.studyschedules.entity.StudySchedule;
import com.mos.backend.studyschedules.infrastructure.StudyScheduleRepository;
import com.mos.backend.users.entity.OauthProvider;
import com.mos.backend.users.entity.User;
import com.mos.backend.users.entity.UserRole;
import com.mos.backend.users.infrastructure.respository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class EntitySaver {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudyRepository studyRepository;
    @Autowired
    private StudyJoinJpaRepository studyJoinJpaRepository;
    @Autowired
    private StudyMemberRepository studyMemberRepository;
    @Autowired
    private StudyScheduleRepository studyScheduleRepository;
    @Autowired
    private StudyCurriculumRepository studyCurriculumRepository;
    @Autowired
    private StudyScheduleCurriculumRepository studyScheduleCurriculumRepository;
    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;
    @Autowired
    private StudyQuestionRepository studyQuestionRepository;
    @Autowired
    private PrivateChatRoomRepository privateChatRoomRepository;
    @Autowired
    private PrivateChatRoomMemberRepository privateChatRoomMemberRepository;
    @Autowired
    private PrivateChatMessageRepository privateChatMessageRepository;
    @Autowired
    private StudyChatRoomRepository studyChatRoomRepository;
    @Autowired
    private StudyChatMessageRepository studyChatMessageRepository;

    public User saveUser() {
        return userRepository.save(
                User.builder()
                        .nickname("nickname")
                        .oauthProvider(OauthProvider.KAKAO)
                        .role(UserRole.USER)
                        .socialId("1")
                        .build()
        );
    }

    public Study saveStudy() {
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

    public StudyJoin saveStudyJoin(User user, Study study, StudyJoinStatus status) {
        return studyJoinJpaRepository.save(
                StudyJoin.builder()
                        .user(user)
                        .study(study)
                        .status(status)
                        .build()
        );
    }

    public StudyQuestion saveStudyQuestion(Study study, Long questionNum) {
        List<String> options = List.of("선택지1", "선택지2", "선택지3");
        return studyQuestionRepository.save(
                StudyQuestion.create(study, questionNum, "질문", "객관식", options, true)
        );
    }

    public QuestionAnswer saveQuestionAnswer(StudyJoin studyJoin, StudyQuestion studyQuestion) {
        return questionAnswerRepository.save(new QuestionAnswer(studyJoin, studyQuestion, "답변"));
    }

    public StudyMember saveStudyMember(User user, Study study) {
        return studyMemberRepository.save(StudyMember.createStudyMember(study, user));
    }

    public StudySchedule saveStudySchedule(Study study) {
        return studyScheduleRepository.save(
                StudySchedule.create(study, "제목", "설명", LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2))
        );
    }

    public StudyCurriculum saveStudyCurriculum(Study study) {
        return studyCurriculumRepository.save(
                StudyCurriculum.create(study, "제목", 1L, "내용")
        );
    }

    public StudyScheduleCurriculum saveStudyScheduleCurriculum(StudySchedule studySchedule, StudyCurriculum studyCurriculum) {
        return studyScheduleCurriculumRepository.save(
                StudyScheduleCurriculum.create(studySchedule, studyCurriculum)
        );
    }

    public PrivateChatRoom savePrivateChatRoom() {
        return privateChatRoomRepository.save(
                PrivateChatRoom.createInvisibleChatRoom(UUID.randomUUID().toString())
        );
    }

    public PrivateChatMessage savePrivateChatMessage(User user, PrivateChatRoom privateChatRoom, String message) {
        return privateChatMessageRepository.save(
                PrivateChatMessage.of(user, privateChatRoom, message)
        );
    }

    public StudyChatRoom saveStudyChatRoom(Study study) {
        String title = study.getTitle();
        return studyChatRoomRepository.save(StudyChatRoom.create(study, title));
    }

    public StudyChatMessage saveStudyChatMessage(User user, StudyChatRoom studyChatRoom, String message) {
        return studyChatMessageRepository.save(
                StudyChatMessage.of(user, studyChatRoom, message)
        );
    }

    public PrivateChatRoomMember savePrivateChatRoomMember(User user, PrivateChatRoom privateChatRoom) {
        return privateChatRoomMemberRepository.save(
                PrivateChatRoomMember.of(privateChatRoom, user)
        );
    }
}
