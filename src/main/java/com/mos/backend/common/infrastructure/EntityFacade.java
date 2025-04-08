package com.mos.backend.common.infrastructure;

import com.mos.backend.attendances.entity.Attendance;
import com.mos.backend.attendances.entity.exception.AttendanceErrorCode;
import com.mos.backend.attendances.infrastructure.AttendanceRepository;
import com.mos.backend.common.exception.MosException;
import com.mos.backend.notifications.entity.NotificationLog;
import com.mos.backend.notifications.entity.exception.NotificationLogErrorCode;
import com.mos.backend.notifications.infrastructure.notificationlog.NotificationLogRepository;
import com.mos.backend.privatechatrooms.entity.PrivateChatRoom;
import com.mos.backend.privatechatrooms.entity.PrivateChatRoomErrorCode;
import com.mos.backend.privatechatrooms.infrastructure.PrivateChatRoomRepository;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studies.entity.exception.StudyErrorCode;
import com.mos.backend.studies.infrastructure.StudyRepository;
import com.mos.backend.studybenefits.entity.StudyBenefit;
import com.mos.backend.studybenefits.entity.exception.StudyBenefitErrorCode;
import com.mos.backend.studybenefits.infrastructure.StudyBenefitRepository;
import com.mos.backend.studychatrooms.entity.StudyChatRoom;
import com.mos.backend.studychatrooms.entity.StudyChatRoomErrorCode;
import com.mos.backend.studychatrooms.infrastructure.StudyChatRoomRepository;
import com.mos.backend.studycurriculum.entity.StudyCurriculum;
import com.mos.backend.studycurriculum.infrastructure.StudyCurriculumRepository;
import com.mos.backend.studyjoins.entity.StudyJoin;
import com.mos.backend.studyjoins.entity.exception.StudyJoinErrorCode;
import com.mos.backend.studyjoins.infrastructure.StudyJoinRepository;
import com.mos.backend.studymaterials.entity.StudyMaterial;
import com.mos.backend.studymaterials.entity.StudyMaterialErrorCode;
import com.mos.backend.studymaterials.infrastructure.StudyMaterialRepository;
import com.mos.backend.studymembers.entity.StudyMember;
import com.mos.backend.studymembers.entity.exception.StudyMemberErrorCode;
import com.mos.backend.studymembers.infrastructure.StudyMemberRepository;
import com.mos.backend.studyquestions.entity.StudyQuestion;
import com.mos.backend.studyquestions.entity.StudyQuestionErrorCode;
import com.mos.backend.studyquestions.infrastructure.StudyQuestionRepository;
import com.mos.backend.studyschedules.entity.StudySchedule;
import com.mos.backend.studyschedules.infrastructure.StudyScheduleRepository;
import com.mos.backend.users.entity.User;
import com.mos.backend.users.entity.exception.UserErrorCode;
import com.mos.backend.users.infrastructure.respository.UserRepository;
import com.mos.backend.userschedules.entity.UserSchedule;
import com.mos.backend.userschedules.infrastructure.UserScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class EntityFacade {
    private final UserRepository userRepository;
    private final StudyRepository studyRepository;
    private final StudyJoinRepository studyJoinRepository;
    private final StudyBenefitRepository studyBenefitRepository;
    private final StudyQuestionRepository studyQuestionRepository;
    private final StudyCurriculumRepository studyCurriculumRepository;
    private final StudyScheduleRepository studyScheduleRepository;
    private final UserScheduleRepository userScheduleRepository;
    private final AttendanceRepository attendanceRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final StudyMaterialRepository studyMaterialRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final PrivateChatRoomRepository privateChatRoomRepository;
    private final StudyChatRoomRepository studyChatRoomRepository;

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new MosException(UserErrorCode.USER_NOT_FOUND));
    }

    public Study getStudy(Long studyId) {
        return studyRepository.findById(studyId)
                .orElseThrow(() -> new MosException(StudyErrorCode.STUDY_NOT_FOUND));
    }

    public StudyJoin getStudyJoin(Long studyApplicationId) {
        return studyJoinRepository.findById(studyApplicationId)
                .orElseThrow(() -> new MosException(StudyJoinErrorCode.STUDY_JOIN_NOT_FOUND));
    }

    public StudyBenefit getStudyBenefit(Long studyBenefitId) {
        return studyBenefitRepository.findById(studyBenefitId)
                .orElseThrow(() -> new MosException(StudyBenefitErrorCode.STUDY_BENEFIT_NOT_FOUND));
    }

    public StudyQuestion getStudyQuestion(Long studyQuestionId) {
        return studyQuestionRepository.findById(studyQuestionId)
                .orElseThrow(() -> new MosException(StudyQuestionErrorCode.STUDY_QUESTION_NOT_FOUND));
    }

    public UserSchedule getUserSchedule(Long userScheduleId) {
        return userScheduleRepository.findById(userScheduleId)
                .orElseThrow(() -> new MosException(UserErrorCode.USER_NOT_FOUND));
    }

    public StudyCurriculum getStudyCurriculum(Long studyCurriculumId) {
        return studyCurriculumRepository.findById(studyCurriculumId)
                .orElseThrow(() -> new MosException(StudyQuestionErrorCode.STUDY_QUESTION_NOT_FOUND));
    }

    public StudySchedule getStudySchedule(Long studyScheduleId) {
        return studyScheduleRepository.findById(studyScheduleId)
                .orElseThrow(() -> new MosException(StudyQuestionErrorCode.STUDY_QUESTION_NOT_FOUND));
    }

    public Attendance getAttendance(Long attendanceId) {
        return attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new MosException(AttendanceErrorCode.ATTENDANCE_NOT_FOUND));
    }

    public StudyMember getStudyMember(Long studyMemberId) {
        return studyMemberRepository.findById(studyMemberId)
                .orElseThrow(() -> new MosException(StudyMemberErrorCode.STUDY_MEMBER_NOT_FOUND));
    }

    public StudyMaterial getStudyMaterial(Long studyMaterialId) {
        return studyMaterialRepository.findById(studyMaterialId).orElseThrow(() -> new MosException(StudyMaterialErrorCode.STUDY_MATERIAL_NOT_FOUND));
    }

    public NotificationLog getNotificationLog(Long notificationId) {
        return notificationLogRepository.findById(notificationId).orElseThrow(() -> new MosException(NotificationLogErrorCode.NOTIFICATION_LOG_NOT_FOUND));
    }

    public PrivateChatRoom getPrivateChatRoom(Long privateChatRoomId) {
        return privateChatRoomRepository.findById(privateChatRoomId)
                .orElseThrow(() -> new MosException(PrivateChatRoomErrorCode.NOT_FOUND));
    }

    public StudyChatRoom getStudyChatRoom(Long studyChatRoomId) {
        return studyChatRoomRepository.findById(studyChatRoomId)
                .orElseThrow(() -> new MosException(StudyChatRoomErrorCode.NOT_FOUND));
    }

}
