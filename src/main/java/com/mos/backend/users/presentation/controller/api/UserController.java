package com.mos.backend.users.presentation.controller.api;

import com.mos.backend.users.application.UserService;
import com.mos.backend.users.application.responsedto.UserDetailRes;
import com.mos.backend.users.presentation.requestdto.UserUpdateReq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;

    @PatchMapping
    public ResponseEntity<Void> update(@AuthenticationPrincipal Long userId, @Valid @RequestBody UserUpdateReq req) {
        userService.update(userId, req);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<UserDetailRes> getDetail(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(userService.getDetail(userId));
    }
}
