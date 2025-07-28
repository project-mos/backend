package com.mos.backend.privatechatrooms.application;

import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.privatechatmessages.application.PrivateChatMessageService;
import com.mos.backend.privatechatmessages.entity.PrivateChatMessage;
import com.mos.backend.privatechatmessages.infrastructure.PrivateChatMessageRepository;
import com.mos.backend.privatechatroommember.application.PrivateChatRoomMemberService;
import com.mos.backend.privatechatrooms.application.res.MyPrivateChatRoomRes;
import com.mos.backend.privatechatrooms.application.res.PrivateChatRoomIdRes;
import com.mos.backend.privatechatrooms.entity.PrivateChatRoom;
import com.mos.backend.privatechatrooms.infrastructure.PrivateChatRoomRepository;
import com.mos.backend.users.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PrivateChatRoomService 테스트")
class PrivateChatRoomServiceTest {
    @Mock
    private EntityFacade entityFacade;
    @Mock
    private PrivateChatRoomRepository privateChatRoomRepository;
    @Mock
    private PrivateChatRoomMemberService privateChatRoomMemberService;
    @Mock
    private PrivateChatMessageService privateChatMessageService;
    @Mock
    private PrivateChatMessageRepository privateChatMessageRepository;
    @Mock
    private PrivateChatRoomInfoService privateChatRoomInfoService;

    @InjectMocks
    private PrivateChatRoomService privateChatRoomService;

    @Nested
    @DisplayName("1:1 채팅방 ID 조회 성공 시나리오")
    class GetPrivateChatRoomIdSuccessScenarios {
        @Test
        @DisplayName("1:1 채팅방이 존재하는 상황의 조회 성공")
        void getPrivateChatRoomId_Success() {
            // Given
            Long userId1 = 1L;
            Long userId2 = 2L;
            User user1 = mock(User.class);
            User user2 = mock(User.class);
            Long privateChatRoomId = 1L;

            when(entityFacade.getUser(userId1)).thenReturn(user1);
            when(entityFacade.getUser(userId2)).thenReturn(user2);
            when(privateChatRoomRepository.findPrivateChatRoomIdByUsers(user1, user2)).thenReturn(Optional.of(privateChatRoomId));

            // When
            PrivateChatRoomIdRes result = privateChatRoomService.getPrivateChatRoomId(userId1, userId2);

            // Then
            assertEquals(privateChatRoomId, result.getPrivateChatRoomId());
            verify(entityFacade).getUser(userId1);
            verify(entityFacade).getUser(userId2);
            verify(privateChatRoomRepository).findPrivateChatRoomIdByUsers(user1, user2);
            verify(privateChatRoomRepository, never()).save(any(PrivateChatRoom.class));
        }

        @Test
        @DisplayName("1:1 채팅방이 존재하지 않으면 생성 후 조회 성공")
        void getPrivateChatRoomId_NotFoundAndCreate() {
            // Given
            Long userId1 = 1L;
            Long userId2 = 2L;
            User user1 = mock(User.class);
            User user2 = mock(User.class);
            PrivateChatRoom privateChatRoom = mock(PrivateChatRoom.class);
            Long privateChatRoomId = 1L;

            when(entityFacade.getUser(userId1)).thenReturn(user1);
            when(entityFacade.getUser(userId2)).thenReturn(user2);
            when(privateChatRoomRepository.findPrivateChatRoomIdByUsers(user1, user2)).thenReturn(Optional.empty());
            when(privateChatRoomRepository.save(any(PrivateChatRoom.class))).thenReturn(privateChatRoom);
            when(privateChatRoom.getId()).thenReturn(privateChatRoomId);

            // When
            PrivateChatRoomIdRes result = privateChatRoomService.getPrivateChatRoomId(userId1, userId2);

            // Then
            assertEquals(privateChatRoomId, result.getPrivateChatRoomId());
            verify(entityFacade).getUser(userId1);
            verify(entityFacade).getUser(userId2);
            verify(privateChatRoomRepository).findPrivateChatRoomIdByUsers(user1, user2);
            verify(privateChatRoomRepository).save(any(PrivateChatRoom.class));
            verify(privateChatRoomMemberService, times(2)).createPrivateChatRoomMember(any(PrivateChatRoom.class), any(User.class));
        }

    }

    @Nested
    @DisplayName("나의 1:1 채팅방 조회 성공 시나리오")
    class GetMyPrivateChatRoomsSuccessScenarios {
        @Test
        @DisplayName("메시지가 존재하는 경우")
        void getMyPrivateChatRooms_WithMessages() {
            // Given
            Long userId = 1L;
            User user = mock(User.class);
            PrivateChatRoom privateChatRoom = mock(PrivateChatRoom.class);
            PrivateChatMessage privateChatMessage = mock(PrivateChatMessage.class);

            when(entityFacade.getUser(userId)).thenReturn(user);
            when(privateChatRoomRepository.findByUser(user)).thenReturn(List.of(privateChatRoom));
            when(privateChatMessageService.getLastMessage(privateChatRoom)).thenReturn(privateChatMessage);

            // When
            List<MyPrivateChatRoomRes> result = privateChatRoomService.getMyPrivateChatRooms(userId);

            // Then
            assertEquals(1, result.size());
            verify(privateChatRoomRepository).findByUser(user);
        }

        @Test
        @DisplayName("채팅방이 없는 경우")
        void getMyPrivateChatRooms_NoChatRooms() {
            // Given
            Long userId = 1L;
            User user = mock(User.class);

            when(entityFacade.getUser(userId)).thenReturn(user);
            when(privateChatRoomRepository.findByUser(user)).thenReturn(Collections.emptyList());

            // When
            List<MyPrivateChatRoomRes> result = privateChatRoomService.getMyPrivateChatRooms(userId);

            // Then
            assertTrue(result.isEmpty());
            verify(privateChatRoomRepository).findByUser(user);
            verify(privateChatMessageRepository, never()).findFirstByPrivateChatRoomOrderByCreatedAtDesc(any(PrivateChatRoom.class));
        }
    }
}