package com.example.blog.api.repository;

import com.example.blog.api.model.Post;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Transactional
public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findPostByTitle(String title);
    List<Post> findPostByContent(String content);
}
