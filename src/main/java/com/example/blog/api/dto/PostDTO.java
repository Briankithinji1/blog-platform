package com.example.blog.api.dto;

import java.time.LocalDateTime;

public record PostDTO(
        Long id,
        String title,
        String content,
        String fullName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
