package com.example.auth.controller;

import com.example.auth.config.SecurityConfig;
import com.example.auth.dto.JwtAuthDto;
import com.example.auth.dto.RefreshTokenDto;
import com.example.auth.dto.UserCredentialsDto;
import com.example.auth.dto.UserDto;
import com.example.auth.jwt.JwtService;
import com.example.auth.model.MyUser;
import com.example.auth.repository.UserRepository;
import com.example.auth.service.AuthService;
import com.example.auth.service.MyUserDetailsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@Import(SecurityConfig.class)
@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthService authService;
    @MockBean
    private JwtService jwtService;

    @MockBean
    private MyUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSignInSuccess() throws Exception {
        UserCredentialsDto creds = new UserCredentialsDto("user", "password");
        JwtAuthDto jwtAuthDto = new JwtAuthDto("access-token", "refresh-token");

        Mockito.when(authService.signIn(any(UserCredentialsDto.class))).thenReturn(jwtAuthDto);

        mockMvc.perform(post("/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    public void testSignInFail() throws Exception {
        UserCredentialsDto creds = new UserCredentialsDto("user", "wrong-password");

        Mockito.when(authService.signIn(any(UserCredentialsDto.class)))
                .thenThrow(new RuntimeException("Bad credentials"));

        mockMvc.perform(post("/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creds)))
                .andExpect(status().is5xxServerError())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Internal error Bad credentials")));
    }

    @Test
    public void testRefreshToken() throws Exception {
        RefreshTokenDto refreshDto = new RefreshTokenDto("refresh-token");
        JwtAuthDto jwtAuthDto = new JwtAuthDto("new-access", "new-refresh");

        Mockito.when(authService.refreshToken(any(RefreshTokenDto.class))).thenReturn(jwtAuthDto);

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh"));
    }

    @Test
    public void testAddUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setPassword("pass123");
        userDto.setEmail("test@example.com");
        userDto.setRoles("ROLE_USER");

        mockMvc.perform(post("/auth/new-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("User added"));

        Mockito.verify(authService).addUser(any(MyUser.class));
    }
}
