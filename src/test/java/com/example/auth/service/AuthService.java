package com.example.auth.service;

import com.example.auth.dto.JwtAuthDto;
import com.example.auth.dto.RefreshTokenDto;
import com.example.auth.dto.UserCredentialsDto;
import com.example.auth.jwt.JwtService;
import com.example.auth.model.MyUser;
import com.example.auth.repository.UserRepository;
import org.apache.tomcat.websocket.AuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddUser_shouldEncodePasswordAndSave() {
        MyUser user = new MyUser();
        user.setPassword("raw");

        when(passwordEncoder.encode("raw")).thenReturn("encoded");

        authService.addUser(user);

        assertEquals("encoded", user.getPassword());
        assertEquals("", user.getRefreshToken());
        verify(repository).save(user);
    }

    @Test
    void testSignIn_success() throws Exception {
        UserCredentialsDto creds = new UserCredentialsDto("user", "password");
        MyUser user = new MyUser();
        user.setName("user");
        user.setPassword("encoded-password");

        when(repository.findByName("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded-password")).thenReturn(true);

        JwtAuthDto token = new JwtAuthDto("access", "refresh");
        when(jwtService.generateAuthToken("user")).thenReturn(token);

        JwtAuthDto result = authService.signIn(creds);

        assertEquals("access", result.getAccessToken());
        assertEquals("refresh", result.getRefreshToken());
        verify(repository).save(user);
    }

    @Test
    void testSignIn_invalidPassword_shouldThrowException() {
        UserCredentialsDto creds = new UserCredentialsDto("user", "wrong");

        MyUser user = new MyUser();
        user.setPassword("encoded");

        when(repository.findByName("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThrows(AuthenticationException.class, () -> authService.signIn(creds));
    }

    @Test
    void testSignIn_userNotFound_shouldThrowException() {
        UserCredentialsDto creds = new UserCredentialsDto("user", "pass");

        when(repository.findByName("user")).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class, () -> authService.signIn(creds));
    }

    @Test
    void testRefreshToken_success() throws Exception {
        String token = "refresh-token";
        String username = "user";

        MyUser user = new MyUser();
        user.setName(username);
        user.setRefreshToken(token);

        JwtAuthDto newTokens = new JwtAuthDto("newAccess", "newRefresh");

        when(jwtService.validateJwtToken(token)).thenReturn(true);
        when(jwtService.getUsernameFromToken(token)).thenReturn(username);
        when(repository.findByName(username)).thenReturn(Optional.of(user));
        when(jwtService.generateAuthToken(username)).thenReturn(newTokens);

        JwtAuthDto result = authService.refreshToken(new RefreshTokenDto(token));

        assertEquals("newAccess", result.getAccessToken());
        assertEquals("newRefresh", result.getRefreshToken());
        verify(repository).save(user);
    }

    @Test
    void testRefreshToken_invalidToken_shouldThrowException() {
        RefreshTokenDto dto = new RefreshTokenDto("bad");

        when(jwtService.validateJwtToken("bad")).thenReturn(false);

        assertThrows(AuthenticationException.class, () -> authService.refreshToken(dto));
    }

    @Test
    void testRefreshToken_tokenMismatch_shouldThrowException() {
        String token = "valid";
        String username = "user";

        MyUser user = new MyUser();
        user.setName(username);
        user.setRefreshToken("different");

        when(jwtService.validateJwtToken(token)).thenReturn(true);
        when(jwtService.getUsernameFromToken(token)).thenReturn(username);
        when(repository.findByName(username)).thenReturn(Optional.of(user));

        assertThrows(AuthenticationException.class, () -> authService.refreshToken(new RefreshTokenDto(token)));
    }

    @Test
    void testFindByUsername_success() {
        MyUser user = new MyUser();
        user.setName("john");

        when(repository.findByName("john")).thenReturn(Optional.of(user));

        MyUser result = authService.findByUsername("john");
        assertEquals("john", result.getName());
    }

    @Test
    void testFindByUsername_notFound_shouldThrow() {
        when(repository.findByName("unknown")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> authService.findByUsername("unknown"));
    }
}
