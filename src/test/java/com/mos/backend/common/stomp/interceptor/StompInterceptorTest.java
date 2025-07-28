package com.mos.backend.common.stomp.interceptor;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.common.stomp.entity.StompErrorCode;
import com.mos.backend.common.stomp.entity.Subscription;
import com.mos.backend.common.stomp.entity.SubscriptionType;
import com.mos.backend.common.utils.StompPrincipalUtil;
import com.mos.backend.common.utils.StompSessionUtil;
import com.mos.backend.privatechatroommember.application.PrivateChatRoomMemberService;
import com.mos.backend.privatechatrooms.application.PrivateChatRoomInfoService;
import com.mos.backend.studychatrooms.application.StudyChatRoomInfoService;
import com.mos.backend.studymembers.application.StudyMemberService;
import com.mos.backend.users.entity.exception.UserErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StompInterceptor 테스트")
class StompInterceptorTest {
    @Mock
    private PrivateChatRoomInfoService privateChatRoomInfoService;
    @Mock
    private PrivateChatRoomMemberService privateChatRoomMemberService;
    @Mock
    private StudyMemberService studyMemberService;
    @Mock
    private StudyChatRoomInfoService studyChatRoomInfoService;
    @InjectMocks
    private StompInterceptor stompInterceptor;
    @Mock
    private MessageChannel messageChannel;

    static final String STOMP_HEADER_USER_ID = "user-id";

    @Nested
    @DisplayName("CONNECT 명령어 처리 성공 시나리오")
    class StompConnectSuccessScenarios {
        @Test
        @DisplayName("Principal의 user-id 추출하여, session에 저장")
        void connectCommandSuccess() {
            // Given
            final Long userId = 123L;
            StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
            accessor.setNativeHeader(STOMP_HEADER_USER_ID, String.valueOf(userId));
            Message<?> message = mock(Message.class);
            accessor.setSessionAttributes(new HashMap<>());
            accessor.setUser(() -> String.valueOf(userId));

            when(message.getHeaders()).thenReturn(accessor.getMessageHeaders());

            // When
            stompInterceptor.preSend(message, messageChannel);

            // Then
            assertThat(StompPrincipalUtil.getUserId(accessor)).isEqualTo(userId);
        }
    }

    @Nested
    @DisplayName("CONNECT 명령어 처리 실패 시나리오")
    class StompConnectFailScenarios {
        @Test
        @DisplayName("Principal에 userId 없는 경우")
        void connectCommandFail() {
            // Given
            StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
            Message<?> message = mock(Message.class);
            accessor.setSessionAttributes(new HashMap<>());

            when(message.getHeaders()).thenReturn(accessor.getMessageHeaders());

            // When
            MosException e = assertThrows(MosException.class, () -> stompInterceptor.preSend(message, messageChannel));

            // Then
            assertThat(e.getErrorCode()).isEqualTo(UserErrorCode.USER_UNAUTHORIZED);
        }
    }

    @Nested
    @DisplayName("SUBSCRIBE 명령어 처리 성공 시나리오")
    class StompSubscribeSuccessScenarios {
        @Test
        @DisplayName("헤더의 destination, subscriptionId로 세션에 subscription 저장")
        void subscribeCommandSuccess() {
            // Given
            final Long userId = 123L;
            final Long privateChatRoomId = 1L;
            final String subscriptionId = "sub-0";
            final String destination = "/sub/private-chat-rooms/" + privateChatRoomId;
            StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
            accessor.setSessionAttributes(new HashMap<>());
            accessor.setNativeHeader(STOMP_HEADER_USER_ID, String.valueOf(userId));
            accessor.setSubscriptionId(subscriptionId);
            accessor.setDestination(destination);
            Message<?> message = mock(Message.class);

            when(message.getHeaders()).thenReturn(accessor.getMessageHeaders());

            // When
            stompInterceptor.preSend(message, messageChannel);

            // Then
            List<Subscription> subscriptions = StompSessionUtil.getAndRemoveAllSubscription(accessor);
            assertThat(subscriptions).hasSize(1);
            Subscription subscription = subscriptions.get(0);
            assertThat(subscription.getType()).isEqualTo(SubscriptionType.PRIVATE_CHAT_ROOM);
            assertThat(subscription.getId()).isEqualTo(privateChatRoomId);
        }
    }

    @Nested
    @DisplayName("SUBSCRIBE 명령어 처리 실패 시나리오")
    class StompSubscribeFailScenarios {
        @Test
        @DisplayName("헤더의 destination이 잘못된 경우")
        void subscribeCommandFailWithInvalidDestination() {
            // Given
            final Long userId = 123L;
            StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
            accessor.setSessionAttributes(new HashMap<>());
            accessor.setNativeHeader(STOMP_HEADER_USER_ID, String.valueOf(userId));
            accessor.setDestination("/sub/invalid-destination");
            Message<?> message = mock(Message.class);

            when(message.getHeaders()).thenReturn(accessor.getMessageHeaders());

            // When
            MosException e = assertThrows(MosException.class, () -> stompInterceptor.preSend(message, messageChannel));

            // Then
            assertThat(e.getErrorCode()).isEqualTo(StompErrorCode.INVALID_DESTINATION);
        }
    }

    @Nested
    @DisplayName("UNSUBSCRIBE 명령어 처리 성공 시나리오")
    class StompUnsubscribeSuccessScenarios {
        @Test
        @DisplayName("헤더의 subscriptionId로 세션에서 subscription 제거 및 서비스 호출")
        void unsubscribeCommandSuccess() {
            // Given
            final Long userId = 123L;
            final Long privateChatRoomId = 10L;
            final String subscriptionId = "sub-0";
            StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.UNSUBSCRIBE);
            accessor.setSessionAttributes(new HashMap<>());
            accessor.setNativeHeader(STOMP_HEADER_USER_ID, String.valueOf(userId));
            accessor.setSubscriptionId(subscriptionId);
            accessor.setUser(() -> String.valueOf(userId));
            Message<?> message = mock(Message.class);

            StompSessionUtil.putSubscription(accessor, Subscription.of(SubscriptionType.PRIVATE_CHAT_ROOM, privateChatRoomId));

            when(message.getHeaders()).thenReturn(accessor.getMessageHeaders());

            // When
            stompInterceptor.preSend(message, messageChannel);

            // Then
            List<Subscription> subscriptions = StompSessionUtil.getAndRemoveAllSubscription(accessor);
            assertThat(subscriptions).isEmpty();
            verify(privateChatRoomMemberService).updateLastEntryAt(userId, privateChatRoomId);
            verify(privateChatRoomInfoService).resetUnreadCount(userId, privateChatRoomId);
        }
    }

    @Nested
    @DisplayName("DISCONNECT 명령어 처리 성공 시나리오")
    class StompDisconnectSuccessScenarios {
        @Test
        @DisplayName("모든 subscription 제거 및 서비스 호출")
        void disconnectCommandSuccess() {
            // Given
            final Long userId = 123L;
            final Long privateChatRoomId = 10L;
            StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.DISCONNECT);
            accessor.setSessionAttributes(new HashMap<>());
            accessor.setNativeHeader(STOMP_HEADER_USER_ID, String.valueOf(userId));
            accessor.getSessionAttributes().put("USER_ID", userId);
            accessor.setSubscriptionId("sub-0");
            accessor.setUser(() -> String.valueOf(userId));

            StompSessionUtil.putSubscription(accessor, Subscription.of(SubscriptionType.PRIVATE_CHAT_ROOM, privateChatRoomId));

            Message<?> message = mock(Message.class);
            when(message.getHeaders()).thenReturn(accessor.getMessageHeaders());

            // When
            stompInterceptor.preSend(message, messageChannel);

            // Then
            List<Subscription> subscriptions = StompSessionUtil.getAndRemoveAllSubscription(accessor);
            assertThat(subscriptions).isEmpty();
            verify(privateChatRoomMemberService).updateLastEntryAt(userId, privateChatRoomId);
            verify(privateChatRoomInfoService).resetChatRoomInfos(userId);
        }
    }
}
