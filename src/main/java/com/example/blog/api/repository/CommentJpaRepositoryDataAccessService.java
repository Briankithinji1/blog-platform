package com.example.blog.api.repository;

import com.example.blog.api.dao.CommentDao;
import com.example.blog.api.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("comment_jpa")
public class CommentJpaRepositoryDataAccessService implements CommentDao {

    private final CommentRepository commentRepository;

    public CommentJpaRepositoryDataAccessService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Optional<Comment> findCommentById(Long id) {
        return commentRepository.findById(id);
    }

    @Override
    public List<Comment> findCommentByContent(String content) {
        return commentRepository.findCommentByContent(content);
    }

    @Override
    public List<Comment> findAllComments() {
        Page<Comment> page = commentRepository.findAll(Pageable.ofSize(1000));
        return page.getContent();
    }

    @Override
    public Comment updateComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public void deleteCommentById(Long id) {
        commentRepository.deleteById(id);
    }
}
