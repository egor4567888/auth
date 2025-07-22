package com.example.auth.dto;

import lombok.Data;

@Data
public class JwtAuthDto {
    private String accessToken;
    private String refreshToken;
}
