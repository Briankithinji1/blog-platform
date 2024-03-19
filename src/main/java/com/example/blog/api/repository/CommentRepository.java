package com.example.blog.api.repository;

import com.example.blog.api.model.Comment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Transactional
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findCommentByContent(String content);
}
