package com.mos.backend.users.presentation.controller.api;

import com.mos.backend.users.application.OauthService;
import com.mos.backend.users.application.responsedto.LoginRes;
import com.mos.backend.users.presentation.requestdto.OauthLoginReq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/oauth2")
public class OauthController {
    private final OauthService oauthService;

    @PostMapping("/login")
    public ResponseEntity<LoginRes> socialLogin(@Valid @RequestBody OauthLoginReq req) {
        LoginRes loginRes = oauthService.login(req);
        return ResponseEntity.ok(loginRes);
    }
}
