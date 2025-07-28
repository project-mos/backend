package com.mos.backend.privatechatmessages.application;

import com.mos.backend.common.EntitySaver;
import com.mos.backend.common.dto.InfinityScrollRes;
import com.mos.backend.privatechatmessages.application.res.PrivateChatMessageRes;
import com.mos.backend.privatechatrooms.entity.PrivateChatRoom;
import com.mos.backend.securityuser.WithMockCustomUser;
import com.mos.backend.testconfig.AbstractTestContainer;
import com.mos.backend.users.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Transactional
@WithMockCustomUser(userId = 1L)
@SpringBootTest
public class PrivateChatMessageServiceTest extends AbstractTestContainer {

    @Autowired
    private EntitySaver entitySaver;
    @Autowired
    private PrivateChatMessageService privateChatMessageService;

    @Nested
    @DisplayName("1:1 채팅 메시지 조회 성공 시나리오")
    class GetPrivateChatMessagesSuccessScenarios {
        @DirtiesContext
        @Test
        @DisplayName("1:1 채팅 메시지 조회 성공")
        void getPrivateChatMessages_Success() {
            // Given
            final int size = 5;
            Long lastPrivateChatMessageId = null;
            User user1 = entitySaver.saveUser();
            User user2 = entitySaver.saveUser();
            PrivateChatRoom privateChatRoom = entitySaver.savePrivateChatRoom();
            entitySaver.savePrivateChatRoomMember(user1, privateChatRoom);
            entitySaver.savePrivateChatRoomMember(user2, privateChatRoom);
            savePrivateChatMessages(user1, privateChatRoom, size);
            savePrivateChatMessages(user2, privateChatRoom, size);

            // When
            InfinityScrollRes<PrivateChatMessageRes> result1 = privateChatMessageService.getPrivateChatMessages(
                    user1.getId(), privateChatRoom.getId(), lastPrivateChatMessageId, size
            );
            InfinityScrollRes<PrivateChatMessageRes> result2 = privateChatMessageService.getPrivateChatMessages(
                    user1.getId(), privateChatRoom.getId(), result1.getLastElementId(), size
            );

            // Then
            List<Long> contentIds1 = result1.getContent().stream().map(PrivateChatMessageRes::getPrivateChatMessageId).toList();

            assertThat(contentIds1).containsExactly(10L, 9L, 8L, 7L, 6L);
            assertThat(result1.getLastElementId()).isEqualTo(6L);
            assertThat(result1.hasNext()).isTrue();

            List<Long> contentIds2 = result2.getContent().stream().map(PrivateChatMessageRes::getPrivateChatMessageId).toList();

            assertThat(contentIds2).containsExactly(5L, 4L, 3L, 2L, 1L);
            assertThat(result2.getLastElementId()).isEqualTo(1L);
            assertThat(result2.hasNext()).isFalse();
        }

        private void savePrivateChatMessages(User user, PrivateChatRoom privateChatRoom, int cnt) {
            for (int i = 0; i < cnt; i++) {
                entitySaver.savePrivateChatMessage(user, privateChatRoom, String.valueOf(i));
            }
        }
    }
}
