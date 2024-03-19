package com.example.blog.api.dao;

import com.example.blog.api.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostDao {
    Post savePost(Post post);
    Optional<Post> findPostById(Long id);
    Optional<Post> findPostByTitle(String title);
    List<Post> findAllPosts();
    List<Post> findPostByContent(String content);
    Post updatePost(Post post);
    void deletePostById(Long id);
}
