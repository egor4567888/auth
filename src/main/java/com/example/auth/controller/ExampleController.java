package com.example.auth.controller;

import com.example.auth.model.MyUser;
import com.example.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController {

    @Autowired
    AuthService service;

    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }

    @GetMapping("/api/guest")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')|| hasAuthority('ROLE_PREMIUM_USER')|| hasAuthority('ROLE_GUEST')")
    public String guest() {
        return "guest";
    }

    @GetMapping("/api/premium_user")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')|| hasAuthority('ROLE_PREMIUM_USER')")
    public String premium_user() {
        return "premium_user";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/api/admin")
    public String admin() {
        return "admin";
    }

    @PostMapping("/new-user")
    public String addUser(@RequestBody MyUser user) {
        service.addUser(user);
        return "User added";
    }
}
