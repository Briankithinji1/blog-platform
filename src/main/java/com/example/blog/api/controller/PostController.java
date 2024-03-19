package com.example.blog.api.controller;

import com.example.blog.api.dto.PostDTO;
import com.example.blog.api.exception.ResourceNotFoundException;
import com.example.blog.api.model.Post;
import com.example.blog.api.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/save")
    public ResponseEntity<?> createPost(
            @RequestBody Post post
    ) {
        try {
            post = postService.createPost(post);
            return ResponseEntity.status(HttpStatus.CREATED).body(post);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(
            @PathVariable("id") Long id
    ) {
        try {
            PostDTO post = postService.getPost(id);
            return ResponseEntity.ok(post);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No post found with ID " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllPosts() {
        try {
            List<PostDTO> posts = postService.getAllPosts();
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @GetMapping("/{title}")
    public ResponseEntity<?> getPostByTitle(
            @PathVariable("post") String title
    ) {
        try {
            Optional<PostDTO> post = postService.getPostByTitle(title);
            if (post.isPresent()) {
                return ResponseEntity.ok(post.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @GetMapping("/{content}")
    public ResponseEntity<?> getPostByContent(
            @PathVariable("content") String content
    ) {
        try {
            List<PostDTO> post = postService.getPostByContent(content);
            return ResponseEntity.ok(content);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> editPost(
            @PathVariable("id") Long id,
            @RequestBody Post post
    ) {
        try {
            Post updatedPost = postService.editPost(id, post);
            return ResponseEntity.ok(updatedPost);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Post found with ID " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @DeleteMapping("/delete/{id}")
    public void deletePost(
            @PathVariable("id") Long id
    ) {
        try {
            postService.deletePostById(id);
        } catch (ResourceNotFoundException e) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("No post found with ID " + id);
        } catch (Exception e) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }
}
