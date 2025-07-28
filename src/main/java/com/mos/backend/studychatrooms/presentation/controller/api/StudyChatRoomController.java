package com.mos.backend.studychatrooms.presentation.controller.api;

import com.mos.backend.studychatrooms.application.StudyChatRoomService;
import com.mos.backend.studychatrooms.application.res.MyStudyChatRoomRes;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class StudyChatRoomController {
    private final StudyChatRoomService studyChatRoomService;

    @GetMapping("/studies/chat-rooms")
    public List<MyStudyChatRoomRes> getMyStudyChatRooms(@AuthenticationPrincipal Long userId) {
        return studyChatRoomService.getMyStudyChatRooms(userId);
    }
}
