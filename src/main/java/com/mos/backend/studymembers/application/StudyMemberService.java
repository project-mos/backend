package com.mos.backend.studymembers.application;

import com.mos.backend.attendances.application.AttendanceService;
import com.mos.backend.attendances.entity.Attendance;
import com.mos.backend.attendances.infrastructure.AttendanceRepository;
import com.mos.backend.common.exception.MosException;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studies.entity.exception.StudyErrorCode;
import com.mos.backend.studychatrooms.entity.StudyChatRoom;
import com.mos.backend.studymembers.application.res.StudyMemberRes;
import com.mos.backend.studymembers.entity.ParticipationStatus;
import com.mos.backend.studymembers.entity.StudyMember;
import com.mos.backend.studymembers.entity.StudyMemberRoleType;
import com.mos.backend.studymembers.entity.exception.StudyMemberErrorCode;
import com.mos.backend.studymembers.infrastructure.StudyMemberRepository;
import com.mos.backend.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyMemberService {
    private final AttendanceService attendanceService;
    private final StudyMemberRepository studyMemberRepository;
    private final AttendanceRepository attendanceRepository;
    private final EntityFacade entityFacade;

    @Transactional
    public void createStudyLeader(Long studyId, Long userId) {
        Study study = entityFacade.getStudy(studyId);
        User user = entityFacade.getUser(userId);

        if (studyMemberRepository.existsByStudyAndRoleType(study, StudyMemberRoleType.LEADER))
            throw new MosException(StudyMemberErrorCode.STUDY_LEADER_ALREADY_EXIST);

        studyMemberRepository.save(StudyMember.createStudyLeader(study, user));
    }

    @Transactional
    public void createStudyMember(Long studyId, Long userId) {
        Study study = entityFacade.getStudy(studyId);
        User user = entityFacade.getUser(userId);

        long studyMemberCnt = countCurrentStudyMember(studyId);

        if (studyMemberCnt == 0)
            throw new MosException(StudyErrorCode.STUDY_NOT_FOUND);

        if (studyMemberCnt >= study.getMaxStudyMemberCount())
            throw new MosException(StudyMemberErrorCode.STUDY_MEMBER_FULL);

        if (studyMemberRepository.existsByUserAndStudy(user, study))
            throw new MosException(StudyMemberErrorCode.CONFLICT);

        studyMemberRepository.save(StudyMember.createStudyMember(study, user));
    }

    @Transactional(readOnly = true)
    public List<StudyMemberRes> getStudyMembers(Long studyId) {
        Study study = entityFacade.getStudy(studyId);

        List<StudyMember> studyMembers = studyMemberRepository.findAllByStudyId(study.getId());
        return studyMembers.stream()
                .map(studyMember -> {
                    List<Attendance> attendances = attendanceRepository.findAllByStudyMemberId(studyMember.getId());
                    LocalDate lastAttendanceDate = getLastAttendanceDate(attendances);
                    double attendanceRate = attendanceService.calculateAttendanceRate(attendances);
                    return StudyMemberRes.of(studyMember, lastAttendanceDate, attendanceRate);
                })
                .toList();
    }

    @PreAuthorize("@studySecurity.isLeaderOrAdmin(#studyId)")
    @Transactional
    public void delegateLeader(Long userId, Long studyId, Long studyMemberId) {
        User user = entityFacade.getUser(userId);
        Study study = entityFacade.getStudy(studyId);
        StudyMember studyMember = entityFacade.getStudyMember(studyMemberId);
        StudyMember expectedStudyLeader = studyMemberRepository.findByUserIdAndStudyId(user.getId(), study.getId())
                .orElseThrow(() -> new MosException(StudyMemberErrorCode.STUDY_MEMBER_NOT_FOUND));

        expectedStudyLeader.changeToMember();
        studyMember.changeToLeader();
    }

    @PreAuthorize("@studySecurity.isMemberOrAdmin(#studyId)")
    @Transactional
    public void withDraw(Long userId, Long studyId) {
        User user = entityFacade.getUser(userId);
        Study study = entityFacade.getStudy(studyId);
        StudyMember studyMember = studyMemberRepository.findByUserIdAndStudyId(userId, studyId)
                .orElseThrow(() -> new MosException(StudyMemberErrorCode.STUDY_MEMBER_NOT_FOUND));

        if (studyMember.isLeader())
            throw new MosException(StudyMemberErrorCode.STUDY_LEADER_WITHDRAW_FORBIDDEN);

        studyMember.withDrawStudy();
    }

    public StudyMember findByStudyAndUser(Study study, User user) {
        return studyMemberRepository.findByStudyAndUser(study, user).orElseThrow(() -> new MosException(StudyMemberErrorCode.STUDY_MEMBER_NOT_FOUND));
    }

    private static LocalDate getLastAttendanceDate(List<Attendance> attendances) {
        return attendances.stream()
                .map(Attendance::getModifiedAt)
                .max(Comparator.naturalOrder())
                .map(LocalDateTime::toLocalDate)
                .orElse(null);
    }

    public int countCurrentStudyMember(Long studyId) {
        Study study = entityFacade.getStudy(studyId);
        List<ParticipationStatus> currentParticipationStatusList = Arrays.asList(ParticipationStatus.ACTIVATED, ParticipationStatus.COMPLETED);
        return studyMemberRepository.countByStudyAndStatusIn(study, currentParticipationStatusList);
    }

    @Transactional
    public void updateLastEntryAt(Long userId, Long studyChatRoomId) {
        StudyChatRoom studyChatRoom = entityFacade.getStudyChatRoom(studyChatRoomId);
        StudyMember studyMember = entityFacade.getStudyMember(userId, studyChatRoom.getStudy().getId());

        studyMember.updateLastEntryAt();
    }

    public List<StudyMember> findAllByUserNotAndStudy(User user, Study study) {
        return studyMemberRepository.findAllByUserNotAndStudy(user, study);
    }
}
