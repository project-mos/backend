package com.mos.backend.users.presentation.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserWebController {

    @GetMapping("/docs/api")
    public String getApiDocs() {
        System.out.println("true = " + true);
        return "api.html";
    }
}
