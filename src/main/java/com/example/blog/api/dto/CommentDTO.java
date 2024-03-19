package com.example.blog.api.dto;

import java.time.LocalDateTime;

public record CommentDTO(
        Long id,
        String content,
        String fullName,
        String title,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
