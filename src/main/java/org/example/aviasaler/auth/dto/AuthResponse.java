package org.example.aviasaler.auth.dto;

public record AuthResponse(String accessToken, String tokenType, long expiresIn, String refreshToken) {
}
