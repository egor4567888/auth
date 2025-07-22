package com.example.auth.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/welcome")
    public String welcome(){
        return "welcome";
    }

    @GetMapping("/api/guest")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')|| hasAuthority('ROLE_PREMIUM_USER')|| hasAuthority('ROLE_GUEST')")
    public String guest(){
        return "guest";
    }

    @GetMapping("/api/premium_user")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')|| hasAuthority('ROLE_PREMIUM_USER')")
    public String premium_user(){
        return "premium_user";
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/api/admin")
    public String admin(){
        return "admin";
    }
}
