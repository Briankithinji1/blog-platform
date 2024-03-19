package com.example.blog.api.config.auth;

public record AuthenticationResponse(
        String accessToken,
        String refreshToken,
        String message
) {
}
