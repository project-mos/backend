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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PrivateChatRoomService {
    private final EntityFacade entityFacade;
    private final PrivateChatRoomRepository privateChatRoomRepository;
    private final PrivateChatMessageRepository privateChatMessageRepository;
    private final PrivateChatRoomMemberService privateChatRoomMemberService;

    private static final String CHAT_ROOM_NAME_FORMAT = "%s,%s";

    @Transactional
    public PrivateChatRoomIdRes getPrivateChatRoomId(Long loginId, Long userId) {
        User user1 = entityFacade.getUser(loginId);
        User user2 = entityFacade.getUser(userId);

        return privateChatRoomRepository.findPrivateChatRoomIdByUsers(user1, user2)
                .map(PrivateChatRoomIdRes::of)
                .orElseGet(() -> saveChatRoomAndMembers(user1, user2));
    }

    private PrivateChatRoomIdRes saveChatRoomAndMembers(User user1, User user2) {
        String chatRoomName = makeChatRoomName(user1, user2);
        PrivateChatRoom privateChatRoom = privateChatRoomRepository.save(PrivateChatRoom.createInvisibleChatRoom(chatRoomName));
        privateChatRoomMemberService.createPrivateChatRoomMember(privateChatRoom, user1);
        privateChatRoomMemberService.createPrivateChatRoomMember(privateChatRoom, user2);
        return PrivateChatRoomIdRes.of(privateChatRoom.getId());
    }

    private static String makeChatRoomName(User user1, User user2) {
        return CHAT_ROOM_NAME_FORMAT.formatted(user1.getNickname(), user2.getNickname());
    }

    @Transactional(readOnly = true)
    public List<MyPrivateChatRoomRes> getMyPrivateChatRooms(Long userId) {
        User user = entityFacade.getUser(userId);
        List<PrivateChatRoom> privateChatRooms = privateChatRoomRepository.findByUser(user);

        List<MyPrivateChatRoomRes> myPrivateChatRoomResList = privateChatRooms.stream()
                .map(privateChatRoom -> {
                    Optional<PrivateChatMessage> optionalPrivateChatMessage = getLastChatMessage(privateChatRoom);
                    int unreadCount = privateChatMessageService.getUnreadCnt(user.getId(), privateChatRoom.getId());
                    return optionalPrivateChatMessage
                            .map(privateChatMessage -> MyPrivateChatRoomRes.of(privateChatRoom, privateChatMessage, unreadCount))
                            .orElse(null);
                })
                .filter(Objects::nonNull)
                .toList();

        return myPrivateChatRoomResList;
    }

    private final PrivateChatMessageService privateChatMessageService;

    private Optional<PrivateChatMessage> getLastChatMessage(PrivateChatRoom privateChatRoom) {
        return privateChatMessageRepository.findFirstByPrivateChatRoomOrderByCreatedAtDesc(privateChatRoom);
    }
}
