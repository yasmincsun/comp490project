/**
 * Class Name: FeedService
 * Date: February 13, 2026
 * @author Jose Bastidas
 *
 */
package com.musicApp.backend.features.feed.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import com.musicApp.backend.features.authentication.repository.AuthenticationUserRepository;
import com.musicApp.backend.features.feed.dto.PostDto;
import com.musicApp.backend.features.feed.model.Comment;
import com.musicApp.backend.features.feed.model.Post;
import com.musicApp.backend.features.feed.repository.CommentRepository;
import com.musicApp.backend.features.feed.repository.PostRepository;

/**
 * This class handles feed-related business logic in the application.
 * It manages creating, editing, deleting, and retrieving posts,
 * as well as adding, editing, deleting, and liking comments and posts.
 */
@Service
public class FeedService {

    private final PostRepository postRepository;
    private final AuthenticationUserRepository userRepository;
    private final CommentRepository commentRepository;

    /**
     * Creates a FeedService object with the required repositories.
     *
     * @param postRepository the repository used to manage posts
     * @param userRepository the repository used to manage users
     * @param commentRepository the repository used to manage comments
     */
    public FeedService(
            PostRepository postRepository,
            AuthenticationUserRepository userRepository,
            CommentRepository commentRepository
    ) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    /**
     * Creates a new post for a user.
     *
     * @param postDto the object containing the post content and picture
     * @param authorId the id of the user creating the post
     * @return the saved {@link Post} object
     */
    public Post createPost(PostDto postDto, Long authorId) {
        AuthenticationUser author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Post post = new Post(postDto.getContent(), author);
        post.setPicture(postDto.getPicture());
        return postRepository.save(post);
    }

    /**
     * Updates an existing post.
     *
     * @param postId the id of the post to update
     * @param userId the id of the user attempting to update the post
     * @param postDto the object containing the updated post content and picture
     * @return the updated {@link Post} object
     */
    public Post editPost(Long postId, Long userId, PostDto postDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        AuthenticationUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!post.getAuthor().equals(user)) {
            throw new IllegalArgumentException("User is not the author of the post");
        }

        post.setContent(postDto.getContent());
        post.setPicture(postDto.getPicture());
        return postRepository.save(post);
    }

    /**
     * Returns feed posts that do not belong to the authenticated user.
     *
     * @param authenticatedUserId the id of the authenticated user
     * @return a list of {@link Post} objects for the user's feed
     */
    public List<Post> getFeedPosts(Long authenticatedUserId) {
        return postRepository.findByAuthorIdNotOrderByCreationDateDesc(authenticatedUserId);
    }

    /**
     * Returns all posts in descending order by creation date.
     *
     * @return a list of all {@link Post} objects
     */
    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreationDateDesc();
    }

    /**
     * Returns a single post by its id.
     *
     * @param postId the id of the post to retrieve
     * @return the matching {@link Post} object
     */
    public Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
    }

    /**
     * Deletes a post if the user is the author.
     *
     * @param postId the id of the post to delete
     * @param userId the id of the user attempting to delete the post
     */
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        AuthenticationUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!post.getAuthor().equals(user)) {
            throw new IllegalArgumentException("User is not the author of the post");
        }

        postRepository.delete(post);
    }

    /**
     * Returns all posts created by a specific user.
     *
     * @param userId the id of the user whose posts will be retrieved
     * @return a list of {@link Post} objects created by the user
     */
    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findByAuthorId(userId);
    }

    /**
     * Adds or removes a like from a post for a user.
     *
     * @param postId the id of the post to like or unlike
     * @param userId the id of the user liking or unliking the post
     * @return the updated {@link Post} object
     */
    public Post likePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        AuthenticationUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (post.getLikes().contains(user)) {
            post.getLikes().remove(user);
        } else {
            post.getLikes().add(user);
        }

        return postRepository.save(post);
    }

    /**
     * Adds a comment to a post.
     *
     * @param postId the id of the post receiving the comment
     * @param userId the id of the user creating the comment
     * @param content the text content of the comment
     * @return the saved {@link Comment} object
     */
    public Comment addComment(Long postId, Long userId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        AuthenticationUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Comment comment = new Comment(post, user, content);
        return commentRepository.save(comment);
    }

    /**
     * Updates an existing comment.
     *
     * @param commentId the id of the comment to update
     * @param userId the id of the user attempting to update the comment
     * @param newContent the updated text content of the comment
     * @return the updated {@link Comment} object
     */
    public Comment editComment(Long commentId, Long userId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        AuthenticationUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!comment.getAuthor().equals(user)) {
            throw new IllegalArgumentException("User is not the author of the comment");
        }

        comment.setContent(newContent);
        return commentRepository.save(comment);
    }

    /**
     * Deletes a comment if the user is the author.
     *
     * @param commentId the id of the comment to delete
     * @param userId the id of the user attempting to delete the comment
     */
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        AuthenticationUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!comment.getAuthor().equals(user)) {
            throw new IllegalArgumentException("User is not the author of the comment");
        }

        commentRepository.delete(comment);
    }
}