package com.mos.backend.users.presentation.controller.api;

import com.mos.backend.users.application.OauthService;
import com.mos.backend.users.presentation.requestdto.OauthLoginReq;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/oauth2")
public class OauthController {
    private final OauthService oauthService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public void socialLogin(@Valid @RequestBody OauthLoginReq req, HttpServletResponse response) {
        oauthService.login(req, response);
    }
}
