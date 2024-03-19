package com.example.blog.api.repository;

import com.example.blog.api.dao.PostDao;
import com.example.blog.api.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository("post_jpa")
public class PostJpaDataAccessService implements PostDao {

    private final PostRepository postRepository;

    public PostJpaDataAccessService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public Post savePost(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Optional<Post> findPostById(Long id) {
        return postRepository.findById(id);
    }

    @Override
    public Optional<Post> findPostByTitle(String title) {
        return postRepository.findPostByTitle(title);
    }

    @Override
    public List<Post> findAllPosts() {
        Page<Post> page = postRepository.findAll(Pageable.ofSize(1000));
        return page.getContent();
    }

    @Override
    public List<Post> findPostByContent(String content) {
        List<Post> posts = postRepository.findPostByContent(content);
        return Objects.requireNonNullElse(posts, Collections.emptyList());
    }

    @Override
    public Post updatePost(Post post) {
        return postRepository.save(post);
    }

    @Override
    public void deletePostById(Long id) {
        postRepository.deleteById(id);
    }
}
