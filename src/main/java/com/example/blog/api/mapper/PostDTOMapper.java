package com.example.blog.api.mapper;

import com.example.blog.api.dto.PostDTO;
import com.example.blog.api.model.Post;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class PostDTOMapper implements Function<Post, PostDTO> {

    @Override
    public PostDTO apply(Post post) {
        return new PostDTO(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getUser().getFullName(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
