package com.example.blog.api.dao;

import com.example.blog.api.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentDao {
    Comment saveComment(Comment comment);
    Optional<Comment> findCommentById(Long id);
    List<Comment> findCommentByContent(String content);
    List<Comment> findAllComments();
    Comment updateComment(Comment comment);
    void deleteCommentById(Long id);
}
