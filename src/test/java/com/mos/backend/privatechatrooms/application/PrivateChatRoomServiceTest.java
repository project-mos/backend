package com.mos.backend.privatechatrooms.application;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.privatechatmessages.entity.PrivateChatMessage;
import com.mos.backend.privatechatmessages.infrastructure.PrivateChatMessageRepository;
import com.mos.backend.privatechatrooms.application.res.PrivateChatRoomRes;
import com.mos.backend.privatechatrooms.entity.PrivateChatRoom;
import com.mos.backend.privatechatrooms.entity.PrivateChatRoomErrorCode;
import com.mos.backend.privatechatrooms.infrastructure.PrivateChatRoomRepository;
import com.mos.backend.privatechatrooms.presentation.req.PrivateChatRoomCreateReq;
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PrivateChatRoomService 테스트")
class PrivateChatRoomServiceTest {
    @Mock
    private EntityFacade entityFacade;
    @Mock
    private PrivateChatRoomRepository privateChatRoomRepository;
    @Mock
    private PrivateChatMessageRepository privateChatMessageRepository;

    @InjectMocks
    private PrivateChatRoomService privateChatRoomService;

    @Nested
    @DisplayName("1:1 채팅방 생성 성공 시나리오")
    class CreatePrivateChatRoomSuccessScenarios {
        @Test
        @DisplayName("1:1 채팅방 생성 성공")
        void createPrivateChatRoom_Success() {
            // Given
            Long userId1 = 1L;
            User user1 = mock(User.class);
            Long userId2 = 2L;
            User user2 = mock(User.class);
            PrivateChatRoomCreateReq req = new PrivateChatRoomCreateReq(userId2);
            Long privateChatRoomId = 1L;
            PrivateChatRoom privateChatRoom = mock(PrivateChatRoom.class);

            when(entityFacade.getUser(userId1)).thenReturn(user1);
            when(entityFacade.getUser(userId2)).thenReturn(user2);
            when(privateChatRoomRepository.findPrivateChatRoomIdByUsers(user1, user2)).thenReturn(Optional.empty());
            when(privateChatRoomRepository.save(any(PrivateChatRoom.class))).thenReturn(privateChatRoom);
            when(privateChatRoom.getId()).thenReturn(privateChatRoomId);

            // When
            privateChatRoomService.create(userId1, req);

            // Then
            verify(entityFacade).getUser(userId1);
            verify(privateChatRoomRepository).save(any(PrivateChatRoom.class));
        }
    }

    @Nested
    @DisplayName("1:1 채팅방 생성 실패 시나리오")
    class CreatePrivateChatRoomFailureScenarios {
        @Test
        @DisplayName("이미 존재하는 1:1 채팅방 생성 시도")
        void createPrivateChatRoom_AlreadyExists() {
            // Given
            Long userId1 = 1L;
            User user1 = mock(User.class);
            Long userId2 = 2L;
            User user2 = mock(User.class);
            PrivateChatRoomCreateReq req = new PrivateChatRoomCreateReq(userId2);

            when(entityFacade.getUser(userId1)).thenReturn(user1);
            when(entityFacade.getUser(userId2)).thenReturn(user2);
            when(privateChatRoomRepository.findPrivateChatRoomIdByUsers(user1, user2)).thenReturn(Optional.of(1L));

            // When
            MosException e = assertThrows(MosException.class, () -> privateChatRoomService.create(userId1, req));

            // Then
            assertEquals(PrivateChatRoomErrorCode.CONFLICT, e.getErrorCode());
            verify(entityFacade).getUser(userId1);
            verify(entityFacade).getUser(userId2);
            verify(privateChatRoomRepository).findPrivateChatRoomIdByUsers(user1, user2);
            verify(privateChatRoomRepository, never()).save(any(PrivateChatRoom.class));
        }
    }

    @Nested
    @DisplayName("1:1 채팅방 조회 성공 시나리오")
    class GetPrivateChatRoomIdSuccessScenarios {
        @Test
        @DisplayName("1:1 채팅방 조회 성공")
        void getPrivateChatRoomId_Success() {
            // Given
            Long userId1 = 1L;
            User user1 = mock(User.class);
            Long userId2 = 2L;
            User user2 = mock(User.class);
            Long privateChatRoomId = 3L;

            when(entityFacade.getUser(userId1)).thenReturn(user1);
            when(entityFacade.getUser(userId2)).thenReturn(user2);
            when(privateChatRoomRepository.findPrivateChatRoomIdByUsers(user1, user2)).thenReturn(Optional.of(privateChatRoomId));

            // When
            Long result = privateChatRoomService.getPrivateChatRoomId(userId1, userId2);

            // Then
            assertEquals(privateChatRoomId, result);
            verify(entityFacade).getUser(userId1);
            verify(entityFacade).getUser(userId2);
            verify(privateChatRoomRepository).findPrivateChatRoomIdByUsers(user1, user2);
        }
    }

    @Nested
    @DisplayName("1:1 채팅방 조회 실패 시나리오")
    class GetPrivateChatRoomIdFailureScenarios {
        @Test
        @DisplayName("1:1 채팅방 조회 실패")
        void getPrivateChatRoomId_NotFound() {
            // Given
            Long userId1 = 1L;
            User user1 = mock(User.class);
            Long userId2 = 2L;
            User user2 = mock(User.class);

            when(entityFacade.getUser(userId1)).thenReturn(user1);
            when(entityFacade.getUser(userId2)).thenReturn(user2);
            when(privateChatRoomRepository.findPrivateChatRoomIdByUsers(user1, user2)).thenReturn(Optional.empty());

            // When
            MosException e = assertThrows(MosException.class, () -> privateChatRoomService.getPrivateChatRoomId(userId1, userId2));

            // Then
            assertEquals(PrivateChatRoomErrorCode.NOT_FOUND, e.getErrorCode());
            verify(entityFacade).getUser(userId1);
            verify(entityFacade).getUser(userId2);
            verify(privateChatRoomRepository).findPrivateChatRoomIdByUsers(user1, user2);
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
            User counterpart = mock(User.class);
            PrivateChatMessage privateChatMessage = mock(PrivateChatMessage.class);

            when(entityFacade.getUser(userId)).thenReturn(user);
            when(privateChatRoomRepository.findByRequesterOrReceiver(user)).thenReturn(List.of(privateChatRoom));
            when(privateChatRoom.getCounterpart(user)).thenReturn(counterpart);
            when(counterpart.getId()).thenReturn(2L);
            when(privateChatMessageRepository.findFirstByPrivateChatRoomOrderByCreatedByDesc(privateChatRoom))
                    .thenReturn(Optional.of(privateChatMessage));

            // When
            List<PrivateChatRoomRes> result = privateChatRoomService.getMyPrivateChatRooms(userId);

            // Then
            assertEquals(1, result.size());
            verify(entityFacade).getUser(userId);
            verify(privateChatRoomRepository).findByRequesterOrReceiver(user);
            verify(privateChatRoom).getCounterpart(user);
            verify(counterpart).getId();
            verify(privateChatMessageRepository).findFirstByPrivateChatRoomOrderByCreatedByDesc(privateChatRoom);
        }

        @Test
        @DisplayName("메시지가 존재하지 않는 경우")
        void getMyPrivateChatRooms_NoMessages() {
            // Given
            Long userId = 1L;
            User user = mock(User.class);
            PrivateChatRoom privateChatRoom = mock(PrivateChatRoom.class);
            User counterpart = mock(User.class);

            when(entityFacade.getUser(userId)).thenReturn(user);
            when(privateChatRoomRepository.findByRequesterOrReceiver(user)).thenReturn(List.of(privateChatRoom));
            when(privateChatRoom.getCounterpart(user)).thenReturn(counterpart);
            when(counterpart.getId()).thenReturn(2L);
            when(privateChatMessageRepository.findFirstByPrivateChatRoomOrderByCreatedByDesc(privateChatRoom))
                    .thenReturn(Optional.empty());

            // When
            List<PrivateChatRoomRes> result = privateChatRoomService.getMyPrivateChatRooms(userId);

            // Then
            assertEquals(1, result.size());
            verify(entityFacade).getUser(userId);
            verify(privateChatRoomRepository).findByRequesterOrReceiver(user);
            verify(privateChatRoom).getCounterpart(user);
            verify(counterpart).getId();
            verify(privateChatMessageRepository).findFirstByPrivateChatRoomOrderByCreatedByDesc(privateChatRoom);
        }

        @Test
        @DisplayName("채팅방이 없는 경우")
        void getMyPrivateChatRooms_NoChatRooms() {
            // Given
            Long userId = 1L;
            User user = mock(User.class);

            when(entityFacade.getUser(userId)).thenReturn(user);
            when(privateChatRoomRepository.findByRequesterOrReceiver(user)).thenReturn(Collections.emptyList());

            // When
            List<PrivateChatRoomRes> result = privateChatRoomService.getMyPrivateChatRooms(userId);

            // Then
            assertTrue(result.isEmpty());
            verify(entityFacade).getUser(userId);
            verify(privateChatRoomRepository).findByRequesterOrReceiver(user);
            verifyNoMoreInteractions(privateChatRoomRepository, privateChatMessageRepository);
        }
    }
}