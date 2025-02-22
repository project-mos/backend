package com.mos.backend.users.presentation.controller.api;

import com.mos.backend.users.application.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/hello/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse hello(@PathVariable Long userId) {
        if (userId == 1L) throw new UserNotFoundException();
        return new UserResponse(userId) ;
    }


    @Data
    @AllArgsConstructor
    static class UserResponse {
        private Long id;

    }
}
