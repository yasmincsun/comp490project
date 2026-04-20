/**
 * Class Name: FeedController
 * Date: February 13, 2026
 * @author Jose Bastidas
 *
 */
package com.musicApp.backend.features.feed.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.musicApp.backend.features.feed.model.Comment;
import com.musicApp.backend.features.feed.model.Post;
import com.musicApp.backend.features.feed.service.FeedService;
import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import com.musicApp.backend.features.feed.dto.CommentDto;
import com.musicApp.backend.features.feed.dto.PostDto;
import com.musicApp.backend.features.feed.repository.PostRepository;

/**
 * This class handles feed-related requests in the application.
 * It provides endpoints for retrieving posts, creating posts,
 * editing posts, deleting posts, liking posts, and managing comments.
 */
@RestController
@RequestMapping("/api/v1/feed")
public class FeedController {

    private final FeedService feedService;

    /**
     * Creates a FeedController object with the required feed service.
     *
     * @param feedService the service used to handle feed operations
     */
    public FeedController(FeedService feedService) {
        this.feedService = feedService;
        //this.postRepository = postRepository;
    }

    /**
     * Returns the feed posts for the authenticated user.
     *
     * @param user the authenticated user taken from the request
     * @return a ResponseEntity containing a list of posts in the user's feed
     */
        @GetMapping
    public ResponseEntity<List<Post>> getFeedPosts(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
        List<Post> posts = feedService.getFeedPosts(user.getId());
        return ResponseEntity.ok(posts);
    }


    /**
     * Creates a new post for the authenticated user.
     *
     * @param postDto the request body containing the post information
     * @param user the authenticated user taken from the request
     * @return a ResponseEntity containing the created post
     */
    @PostMapping("/posts")
    public ResponseEntity<Post> createPost(
            @RequestBody PostDto postDto,
            @RequestAttribute("authenticatedUser") AuthenticationUser user
    ) {
        Post post = feedService.createPost(postDto, user.getId());
        return ResponseEntity.ok(post);
    }

    /**
     * Updates an existing post.
     *
     * @param postId the id of the post to update
     * @param postDto the request body containing the updated post information
     * @param user the authenticated user taken from the request
     * @return a ResponseEntity containing the updated post
     */
    @PutMapping("/posts/{postId}")
    public ResponseEntity<Post> editPost(
            @PathVariable Long postId,
            @RequestBody PostDto postDto,
            @RequestAttribute("authenticatedUser") AuthenticationUser user
    ) {
        Post post = feedService.editPost(postId, user.getId(), postDto);
        return ResponseEntity.ok(post);
    }

    /**
     * Returns a single post by its id.
     *
     * @param postId the id of the post to retrieve
     * @return a ResponseEntity containing the requested post
     */
    @GetMapping("/posts/{postId}")
    public ResponseEntity<Post> getPost(@PathVariable Long postId) {
        Post post = feedService.getPost(postId);
        return ResponseEntity.ok(post);
    }

    /**
     * Deletes a post owned by the authenticated user.
     *
     * @param postId the id of the post to delete
     * @param user the authenticated user taken from the request
     * @return a ResponseEntity with no content after deletion
     */
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @RequestAttribute("authenticatedUser") AuthenticationUser user
    ) {
        feedService.deletePost(postId, user.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Returns all posts created by a specific user.
     *
     * @param userId the id of the user whose posts will be retrieved
     * @return a ResponseEntity containing a list of posts by the user
     */
    @GetMapping("/posts/user/{userId}")
    public ResponseEntity<List<Post>> getPostsByUserId(@PathVariable Long userId) {
        List<Post> posts = feedService.getPostsByUserId(userId);
        return ResponseEntity.ok(posts);
    }

    /**
     * Likes a post for the authenticated user.
     *
     * @param postId the id of the post to like
     * @param user the authenticated user taken from the request
     * @return a ResponseEntity containing the updated post
     */
    @PutMapping("/posts/{postId}/like")
    public ResponseEntity<Post> likePost(
            @PathVariable Long postId,
            @RequestAttribute("authenticatedUser") AuthenticationUser user
    ) {
        Post post = feedService.likePost(postId, user.getId());
        return ResponseEntity.ok(post);
    }

    /**
     * Adds a comment to a post.
     *
     * @param postId the id of the post being commented on
     * @param commentDto the request body containing the comment content
     * @param user the authenticated user taken from the request
     * @return a ResponseEntity containing the created comment
     */
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Comment> addComment(
            @PathVariable Long postId,
            @RequestBody CommentDto commentDto,
            @RequestAttribute("authenticatedUser") AuthenticationUser user
    ) {
        Comment comment = feedService.addComment(postId, user.getId(), commentDto.getContent());
        return ResponseEntity.ok(comment);
    }

    /**
     * Deletes a comment owned by the authenticated user.
     *
     * @param commentId the id of the comment to delete
     * @param user the authenticated user taken from the request
     * @return a ResponseEntity with no content after deletion
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @RequestAttribute("authenticatedUser") AuthenticationUser user
    ) {
        feedService.deleteComment(commentId, user.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates an existing comment.
     *
     * @param commentId the id of the comment to update
     * @param commentDto the request body containing the updated comment content
     * @param user the authenticated user taken from the request
     * @return a ResponseEntity containing the updated comment
     */
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Comment> editComment(
            @PathVariable Long commentId,
            @RequestBody CommentDto commentDto,
            @RequestAttribute("authenticatedUser") AuthenticationUser user
    ) {
        Comment comment = feedService.editComment(commentId, user.getId(), commentDto.getContent());
        return ResponseEntity.ok(comment);
    }
}