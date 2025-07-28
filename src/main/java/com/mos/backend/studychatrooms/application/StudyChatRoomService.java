package com.mos.backend.studychatrooms.application;

import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studychatmessages.application.StudyChatMessageService;
import com.mos.backend.studychatmessages.entity.StudyChatMessage;
import com.mos.backend.studychatrooms.application.res.MyStudyChatRoomRes;
import com.mos.backend.studychatrooms.entity.StudyChatRoom;
import com.mos.backend.studychatrooms.infrastructure.StudyChatRoomRepository;
import com.mos.backend.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StudyChatRoomService {
    private final EntityFacade entityFacade;
    private final StudyChatMessageService studyChatMessageService;
    private final StudyChatRoomRepository studyChatRoomRepository;
    private final StudyChatRoomInfoService studyChatRoomInfoService;

    @Transactional
    public void create(Long studyId) {
        Study study = entityFacade.getStudy(studyId);
        String title = study.getTitle();

        StudyChatRoom studyChatRoom = StudyChatRoom.create(study, title);

        studyChatRoomRepository.save(studyChatRoom);
    }

    @Transactional(readOnly = true)
    public List<MyStudyChatRoomRes> getMyStudyChatRooms(Long userId) {
        User user = entityFacade.getUser(userId);
        List<StudyChatRoom> studyChatRooms = studyChatRoomRepository.findAllByUserId(user.getId());

        List<MyStudyChatRoomRes> myStudyChatRoomResList = studyChatRooms.stream()
                .map(studyChatRoom -> {
                    int unreadCount = studyChatMessageService.getUnreadCnt(user.getId(), studyChatRoom.getId());

                    StudyChatMessage studyChatMessage = studyChatMessageService.getLastMessage(studyChatRoom);

                    return MyStudyChatRoomRes.of(
                            studyChatRoom.getId(),
                            studyChatRoom.getName(),
                            studyChatMessage.getMessage(),
                            studyChatMessage.getCreatedAt(),
                            unreadCount
                    );
                })
                .toList();

        studyChatRoomInfoService.cachingStudyChatRoomInfos(userId, myStudyChatRoomResList);

        return myStudyChatRoomResList;
    }
}
