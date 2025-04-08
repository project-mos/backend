package com.mos.backend.notifications.presentation.controller.api;

import com.mos.backend.notifications.application.UserFcmTokenService;
import com.mos.backend.notifications.presentation.requestdto.UserFcmTokenRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class UserFcmTokenController {

    private final UserFcmTokenService userFcmTokenService;

    @PostMapping("/fcm/tokens")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void create(@RequestBody UserFcmTokenRequestDto userFcmTokenRequestDto, @AuthenticationPrincipal long userId) {
        userFcmTokenService.create(userId, userFcmTokenRequestDto.getFcmToken());
    }

    @DeleteMapping("/fcm/tokens")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestBody UserFcmTokenRequestDto userFcmTokenRequestDto, @AuthenticationPrincipal long userId) {
        System.out.println("userFcmTokenRequestDto = " + userFcmTokenRequestDto.getFcmToken());
        userFcmTokenService.delete(userId, userFcmTokenRequestDto.getFcmToken());
    }
}
