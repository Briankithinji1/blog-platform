package com.example.blog.api.service;

import com.example.blog.api.dao.PostDao;
import com.example.blog.api.dto.PostDTO;
import com.example.blog.api.exception.ResourceNotFoundException;
import com.example.blog.api.mapper.PostDTOMapper;
import com.example.blog.api.model.Post;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostDao postDao;
    private final PostDTOMapper postDTOMapper;

    public PostService(PostDao postDao, PostDTOMapper postDTOMapper) {
        this.postDao = postDao;
        this.postDTOMapper = postDTOMapper;
    }

    public Post createPost(Post post) {

        // Set createdAt to current time
        LocalDateTime currentTime = LocalDateTime.now();
        post.setCreatedAt(currentTime);

        // Do not set updatedAt during creation (leave it null)
        post.setUpdatedAt(null);

        return postDao.savePost(post);
    }

    public PostDTO getPost(Long id) {
        return postDao.findPostById(id)
                .map(postDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Post with ID [%s] not found".formatted(id)
                ));
    }

    public List<PostDTO> getAllPosts() {
        return postDao.findAllPosts()
                .stream()
                .map(postDTOMapper)
                .collect(Collectors.toList());
    }

    public Optional<PostDTO> getPostByTitle(String title) {
        return postDao.findPostByTitle(title)
                .map(postDTOMapper);
    }

    public List<PostDTO> getPostByContent(String content) {
        return postDao.findPostByContent(content)
                .stream()
                .map(postDTOMapper)
                .collect(Collectors.toList());
    }

    public Post editPost(Long id, Post post) {
        Post existingPost = postDao.findPostById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "post with Id [%s] not found".formatted(id)
                ));

        if (post.getTitle() != null && !post.getTitle().equals(existingPost.getTitle())) {
            existingPost.setTitle(post.getTitle());
        }

        if (post.getContent() != null && !post.getContent().equals(existingPost.getContent())) {
            existingPost.setContent(post.getContent());
        }

        existingPost.setUpdatedAt(LocalDateTime.now());

        return postDao.updatePost(existingPost);
    }

    public void deletePostById(Long id) {
        try {
            postDao.deletePostById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new ResourceNotFoundException(
                    "Post with Id [%s] not found".formatted(id)
            );
        }
    }
}
