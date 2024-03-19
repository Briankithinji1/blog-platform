package com.example.blog.api.config.auth;

public record RegisterRequest(
        String full_name,
        String email,
        String password
) {
}
