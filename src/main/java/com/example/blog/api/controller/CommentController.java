package com.example.blog.api.controller;

import com.example.blog.api.dto.CommentDTO;
import com.example.blog.api.exception.ResourceNotFoundException;
import com.example.blog.api.model.Comment;
import com.example.blog.api.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/save")
    public ResponseEntity<?> createComment(
            @RequestBody Comment comment
            ) {
        try {
            comment = commentService.createComment(comment);
            return ResponseEntity.status(HttpStatus.CREATED).body(comment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getComment(
            @PathVariable("id") Long id
    ) {
        try {
            CommentDTO comment = commentService.getComment(id);
            return ResponseEntity.ok(comment);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No comment found with ID " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllComments() {
        try {
            List<CommentDTO> comments = commentService.getAllComments();
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @GetMapping("/{content}")
    public ResponseEntity<?> getCommentByContent(
            @PathVariable("content") String content
    ) {
        try {
            List<CommentDTO> comment = commentService.getCommentByContent(content);
            return ResponseEntity.ok(comment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> editComment(
            @PathVariable("id") Long id,
            @RequestBody Comment comment
    ) {
        try {
            Comment updatedComment = commentService.editComment(id, comment);
            return ResponseEntity.ok(updatedComment);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No comment found with ID " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @DeleteMapping("/delete/{id}")
    public void deleteComment(
            @PathVariable("id") Long id
    ) {
        try {
            commentService.deleteCommentById(id);
        } catch (ResourceNotFoundException e) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("No comment found with ID " + id);
        } catch (Exception e) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }
}
