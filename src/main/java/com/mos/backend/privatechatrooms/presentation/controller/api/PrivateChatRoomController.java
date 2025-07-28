package com.mos.backend.privatechatrooms.presentation.controller.api;

import com.mos.backend.privatechatrooms.application.PrivateChatRoomService;
import com.mos.backend.privatechatrooms.application.res.MyPrivateChatRoomRes;
import com.mos.backend.privatechatrooms.application.res.PrivateChatRoomIdRes;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PrivateChatRoomController {
    private final PrivateChatRoomService privateChatRoomService;

    @GetMapping("/users/{userId}/private-chat-rooms")
    public PrivateChatRoomIdRes getPrivateChatRoomId(@AuthenticationPrincipal Long loginId, @PathVariable Long userId) {
        return privateChatRoomService.getPrivateChatRoomId(loginId, userId);
    }

    @GetMapping("/private-chat-rooms")
    public List<MyPrivateChatRoomRes> getMyPrivateChatRooms(@AuthenticationPrincipal Long userId) {
        return privateChatRoomService.getMyPrivateChatRooms(userId);
    }
}
