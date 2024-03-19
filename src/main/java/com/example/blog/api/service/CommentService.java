package com.example.blog.api.service;

import com.example.blog.api.dao.CommentDao;
import com.example.blog.api.dto.CommentDTO;
import com.example.blog.api.exception.ResourceNotFoundException;
import com.example.blog.api.mapper.CommentDTOMapper;
import com.example.blog.api.model.Comment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentDao commentDao;
    private final CommentDTOMapper dtoMapper;

    public CommentService(CommentDao commentDao, CommentDTOMapper dtoMapper) {
        this.commentDao = commentDao;
        this.dtoMapper = dtoMapper;
    }

    public Comment createComment(Comment comment) {

        // Set createdAt to current time
        LocalDateTime currentTime = LocalDateTime.now();
        comment.setCreatedAt(currentTime);

        // Do not set updatedAt during creation (leave it null)
        comment.setUpdatedAt(null);

        return commentDao.saveComment(comment);
    }

    public CommentDTO getComment(Long id) {
        return commentDao.findCommentById(id)
                .map(dtoMapper)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comment with ID [%s] not found".formatted(id)
                ));
    }

    public List<CommentDTO> getAllComments() {
        return commentDao.findAllComments()
                .stream()
                .map(dtoMapper)
                .collect(Collectors.toList());
    }

    public List<CommentDTO> getCommentByContent(String content) {
        return commentDao.findCommentByContent(content)
                .stream()
                .map(dtoMapper)
                .collect(Collectors.toList());
    }

    public Comment editComment(Long id, Comment comment) {
        Comment existingComment = commentDao.findCommentById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comment with ID [%s] not found".formatted(id)
                ));

        if (comment.getContent() != null && !comment.getContent().equals(existingComment.getContent())) {
            existingComment.setContent(comment.getContent());
        }

        existingComment.setUpdatedAt(LocalDateTime.now());

        return commentDao.updateComment(existingComment);
    }

    public void deleteCommentById(Long id) {
        try {
            commentDao.deleteCommentById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new ResourceNotFoundException(
                    "Comment with Id [%s] not found".formatted(id)
            );
        }
    }
}
