package com.musicApp.backend.features.feed.controller;

import com.musicApp.backend.features.feed.model.Post;
import com.musicApp.backend.features.feed.repository.PostRepository;
import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import com.musicApp.backend.features.authentication.repository.AuthenticationUserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.security.Principal;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostRepository postRepository;
    private final AuthenticationUserRepository userRepository;

    public PostController(PostRepository postRepository, AuthenticationUserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    /*@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPost(
            @RequestParam("content") String content,
            @RequestParam(value = "picture", required = false) MultipartFile picture,
            Principal principal
    ) {
        // Verify user is authenticated
        if (principal == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }

        String username = principal.getName();

        AuthenticationUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setContent(content);
        post.setAuthor(user);

        // Only set picture if file exists and is not empty
        if (picture != null && !picture.isEmpty()) {
            post.setPicture(picture.getOriginalFilename());
        }

        postRepository.save(post);

        return ResponseEntity.ok("Post created");
    }*/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<?> createPost(
        @RequestParam("content") String content,
        @RequestParam(value = "picture", required = false) MultipartFile picture
) {
    // TEMPORARY: use first user in database for testing
    AuthenticationUser user = userRepository.findAll().stream().findFirst()
            .orElseThrow(() -> new RuntimeException("No users in DB"));

    Post post = new Post();
    post.setContent(content);
    post.setAuthor(user);

    if (picture != null && !picture.isEmpty()) {
        post.setPicture(picture.getOriginalFilename());
    }

    Post savedPost = postRepository.save(post);

    return ResponseEntity.ok("Post created with ID: " + savedPost.getId());
}
}