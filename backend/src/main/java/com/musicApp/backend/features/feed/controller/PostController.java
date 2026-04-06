package com.musicApp.backend.features.feed.controller;

import com.musicApp.backend.features.feed.model.Post;
import com.musicApp.backend.features.feed.repository.PostRepository;
import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import com.musicApp.backend.features.authentication.repository.AuthenticationUserRepository;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostRepository postRepository;
    private final AuthenticationUserRepository userRepository;

    public PostController(PostRepository postRepository, AuthenticationUserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPost(
            @RequestParam("content") String content,
            @RequestParam(value = "picture", required = false) MultipartFile picture,
            @RequestAttribute(value = "authenticatedUser", required = false) AuthenticationUser authenticatedUser
    ) {
        if (authenticatedUser == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }

        AuthenticationUser user = userRepository.findByEmail(authenticatedUser.getEmail())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        Post post = new Post();
        post.setContent(content);
        post.setAuthor(user);

        if (picture != null && !picture.isEmpty()) {
            post.setPicture(picture.getOriginalFilename());
        }

        postRepository.save(post);

        return ResponseEntity.ok("Post created");
    }
}