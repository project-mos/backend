package com.mos.backend.studychatrooms.application;

import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studychatmessages.application.StudyChatMessageService;
import com.mos.backend.studychatmessages.entity.StudyChatMessage;
import com.mos.backend.studychatrooms.entity.StudyChatRoom;
import com.mos.backend.studychatrooms.infrastructure.StudyChatRoomRepository;
import com.mos.backend.users.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudyChatRoomService 테스트")
class StudyChatRoomServiceTest {

    @Mock
    private EntityFacade entityFacade;
    @Mock
    private StudyChatRoomRepository studyChatRoomRepository;
    @Mock
    private StudyChatMessageService studyChatMessageService;
    @Mock
    private StudyChatRoomInfoService studyChatRoomInfoService;

    @InjectMocks
    private StudyChatRoomService studyChatRoomService;

    @Nested
    @DisplayName("스터디 채팅방 생성 성공 시나리오")
    class CreateStudyChatRoomSuccessScenarios {
        @Test
        @DisplayName("스터디 채팅방 생성 성공")
        void createStudyChatRoom_Success() {
            // Given
            Long studyId = 1L;
            Study study = mock(Study.class);

            when(entityFacade.getStudy(studyId)).thenReturn(study);

            // When
            studyChatRoomService.create(studyId);

            // Then
            verify(studyChatRoomRepository).save(any(StudyChatRoom.class));
        }
    }

    @Nested
    @DisplayName("스터디 채팅방 조회 성공 시나리오")
    class GetMyStudyChatRoomsSuccessScenarios {
        @Test
        @DisplayName("내 스터디 채팅방 조회 성공")
        void getMyStudyChatRooms_Success() {
            // Given
            final Long userId = 1L;
            final Long studyChatRoomId = 1L;
            final int unreadCount = 2;

            User user = mock(User.class);
            StudyChatRoom studyChatRoom = mock(StudyChatRoom.class);
            StudyChatMessage studyChatMessage = mock(StudyChatMessage.class);

            when(entityFacade.getUser(userId)).thenReturn(user);
            when(user.getId()).thenReturn(userId);
            when(studyChatRoomRepository.findAllByUserId(userId)).thenReturn(List.of(studyChatRoom));
            when(studyChatRoom.getId()).thenReturn(1L);
            when(studyChatMessageService.getUnreadCnt(userId, studyChatRoomId)).thenReturn(unreadCount);
            when(studyChatMessageService.getLastMessage(studyChatRoom)).thenReturn(studyChatMessage);

            // When
            studyChatRoomService.getMyStudyChatRooms(userId);

            // Then
            verify(studyChatRoomRepository).findAllByUserId(userId);
            verify(studyChatMessageService).getUnreadCnt(userId, studyChatRoomId);
        }
    }
}