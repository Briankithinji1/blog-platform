package com.example.blog.api.mapper;

import com.example.blog.api.dto.CommentDTO;
import com.example.blog.api.model.Comment;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class CommentDTOMapper implements Function<Comment, CommentDTO> {

    @Override
    public CommentDTO apply(Comment comment) {
        return new CommentDTO(
                comment.getId(),
                comment.getContent(),
                comment.getUser().getFullName(),
                comment.getPost().getTitle(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
