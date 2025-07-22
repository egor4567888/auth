package com.example.auth.controller;

import com.example.auth.dto.JwtAuthDto;
import com.example.auth.dto.RefreshTokenDto;
import com.example.auth.dto.UserCredentialsDto;
import com.example.auth.model.MyUser;
import com.example.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService service;

    @PostMapping("/signIn")
    public ResponseEntity<JwtAuthDto> signIn(@RequestBody UserCredentialsDto userCredentialsDto) {
        try {
            JwtAuthDto jwtAuthDto = service.signIn(userCredentialsDto);
            return ResponseEntity.ok(jwtAuthDto);
        } catch (AuthenticationException e) {
            throw new RuntimeException("Authentication failed " + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public JwtAuthDto refresh(@RequestBody RefreshTokenDto refreshTokenDto) throws Exception {
        return service.refreshToken(refreshTokenDto);
    }

    //для упрощения структуры эндпоинт помещён в auth
    @PostMapping("/new-user")
    public String addUser(@RequestBody MyUser user) {
        service.addUser(user);
        return "User added";
    }
}