package com.example.blog.api.config.auth;

public record LoginRequest(
        String email,
        String password
) {
}
