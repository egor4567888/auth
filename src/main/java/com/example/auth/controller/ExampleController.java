package com.example.auth.controller;

import com.example.auth.dto.JwtAuthDto;
import com.example.auth.dto.UserCredentialsDto;
import com.example.auth.model.MyUser;
import com.example.auth.service.AuthService;
import com.example.auth.service.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ExampleController {

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


}
