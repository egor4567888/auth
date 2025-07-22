package com.example.auth.jwt;

import com.example.auth.dto.JwtAuthDto;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private final String username = "test_user";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        //@Value не работает в тестах без Spring Context
        String secret = "948e11fec76d21d950e5018e882160990a300d37b81d48322293a95b8111e374ee27b50c680b1096946870ef68a463fe64ba006f3a359def816171412b393605";
        ReflectionTestUtils.setField(jwtService, "jwtSecret", secret);
    }

    @Test
    void testGenerateAuthToken_shouldReturnValidTokens() {
        JwtAuthDto tokens = jwtService.generateAuthToken(username);

        assertNotNull(tokens);
        assertNotNull(tokens.getAccessToken());
        assertNotNull(tokens.getRefreshToken());
    }

    @Test
    void testGetUsernameFromToken_shouldReturnCorrectUsername() {
        String token = jwtService.generateAuthToken(username).getAccessToken();

        String extractedUsername = jwtService.getUsernameFromToken(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    void testValidateJwtToken_withValidToken_shouldReturnTrue() {
        String token = jwtService.generateAuthToken(username).getAccessToken();

        boolean isValid = jwtService.validateJwtToken(token);

        assertTrue(isValid);
    }

    @Test
    void testValidateJwtToken_withInvalidToken_shouldReturnFalse() {
        String fakeToken = "invalid.token.value";

        boolean isValid = jwtService.validateJwtToken(fakeToken);

        assertFalse(isValid);
    }

    @Test
    void testRefreshAccessToken_shouldReturnNewTokens() {
        JwtAuthDto refreshed = jwtService.refreshAccessToken(username, "dummyRefresh");

        assertNotNull(refreshed.getAccessToken());
        assertNotNull(refreshed.getRefreshToken());
        assertNotEquals("dummyRefresh", refreshed.getRefreshToken());
    }
}
