package com.example.auth.jwt;


import com.example.auth.dto.JwtAuthDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


@Service
public class JwtService {

    private static final Logger log = LogManager.getLogger(JwtService.class);

    @Value("948e11fec76d21d950e5018e882160990a300d37b81d48322293a95b8111e374ee27b50c680b1096946870ef68a463fe64ba006f3a359def816171412b393605")
    private String jwtSecret;

    public JwtAuthDto generateAuthToken(String username) {
        JwtAuthDto jwtAuthDto = new JwtAuthDto();
        jwtAuthDto.setAccessToken(generateAccessToken(username));
        jwtAuthDto.setRefreshToken(generateRefreshToken(username));
        return jwtAuthDto;
    }

    private String generateAccessToken(String username) {
        Date date = Date.from(LocalDateTime.now().plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(username)
                .expiration(date)
                .signWith(getSignInKey())
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser().
                verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch (ExpiredJwtException ex) {
            log.error("Expired JwtException", ex);
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JwtException", ex);
        } catch (MalformedJwtException ex) {
            log.error("Malformed JwtException", ex);
        } catch (SecurityException ex) {
            log.error("Security Exception", ex);
        } catch (Exception ex) {
            log.error("invalid token", ex);
        }
        return false;
    }

    public JwtAuthDto refreshAccessToken(String username, String refreshToken) {
        JwtAuthDto jwtAuthDto = new JwtAuthDto();
        jwtAuthDto.setAccessToken(generateAccessToken(username));
        jwtAuthDto.setRefreshToken(generateRefreshToken(username));
        return jwtAuthDto;
    }

    private String generateRefreshToken(String username) {
        Date date = Date.from(LocalDateTime.now().plusHours(12).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(username)
                .expiration(date)
                .signWith(getSignInKey())
                .compact();
    }


    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
