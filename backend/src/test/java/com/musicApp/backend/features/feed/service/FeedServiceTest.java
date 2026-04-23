package com.musicApp.backend.features.feed.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import com.musicApp.backend.features.authentication.repository.AuthenticationUserRepository;
import com.musicApp.backend.features.feed.dto.PostDto;
import com.musicApp.backend.features.feed.model.Comment;
import com.musicApp.backend.features.feed.model.Post;
import com.musicApp.backend.features.feed.repository.CommentRepository;
import com.musicApp.backend.features.feed.repository.PostRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class FeedServiceTest {

    private PostRepository postRepository;
    private AuthenticationUserRepository userRepository;
    private CommentRepository commentRepository;
    private FeedService feedService;

    private AuthenticationUser author;
    private AuthenticationUser otherUser;

    @BeforeEach
    void setUp() {
        postRepository = Mockito.mock(PostRepository.class);
        userRepository = Mockito.mock(AuthenticationUserRepository.class);
        commentRepository = Mockito.mock(CommentRepository.class);

        feedService = new FeedService(postRepository, userRepository, commentRepository);

        author = new AuthenticationUser();
        author.setId(1L);
        author.setUsername("authorUser");

        otherUser = new AuthenticationUser();
        otherUser.setId(2L);
        otherUser.setUsername("otherUser");
    }

    @Test
    void createPost_shouldSavePost_whenUserExists() {
        PostDto postDto = mock(PostDto.class);
        when(postDto.getContent()).thenReturn("Hello world");
        when(postDto.getPicture()).thenReturn("image.jpg");

        when(userRepository.findById(any())).thenReturn(Optional.of(author));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post result = feedService.createPost(postDto, 1L);

        assertEquals("Hello world", result.getContent());
        assertEquals("image.jpg", result.getPicture());
        assertEquals(author, result.getAuthor());
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void createPost_shouldThrowException_whenUserDoesNotExist() {
        PostDto postDto = mock(PostDto.class);
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> feedService.createPost(postDto, 1L)
        );

        assertEquals("User not found", ex.getMessage());
        verify(postRepository, never()).save(any());
    }

    @Test
    void editPost_shouldUpdateAndSave_whenUserIsAuthor() {
        PostDto postDto = mock(PostDto.class);
        when(postDto.getContent()).thenReturn("Updated content");
        when(postDto.getPicture()).thenReturn("updated.jpg");

        Post post = new Post("Old content", author);
        post.setPicture("old.jpg");

        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(userRepository.findById(any())).thenReturn(Optional.of(author));
        when(postRepository.save(post)).thenReturn(post);

        Post result = feedService.editPost(10L, 1L, postDto);

        assertEquals("Updated content", result.getContent());
        assertEquals("updated.jpg", result.getPicture());
        verify(postRepository).save(post);
    }

    @Test
    void editPost_shouldThrowException_whenPostDoesNotExist() {
        PostDto postDto = mock(PostDto.class);
        when(postRepository.findById(any())).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> feedService.editPost(10L, 1L, postDto)
        );

        assertEquals("Post not found", ex.getMessage());
    }

    @Test
    void editPost_shouldThrowException_whenUserDoesNotExist() {
        PostDto postDto = mock(PostDto.class);
        Post post = new Post("Old content", author);

        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> feedService.editPost(10L, 1L, postDto)
        );

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void editPost_shouldThrowException_whenUserIsNotAuthor() {
        PostDto postDto = mock(PostDto.class);
        Post post = new Post("Old content", author);

        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(userRepository.findById(any())).thenReturn(Optional.of(otherUser));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> feedService.editPost(10L, 2L, postDto)
        );

        assertEquals("User is not the author of the post", ex.getMessage());
        verify(postRepository, never()).save(any());
    }

    @Test
    void getFeedPosts_shouldReturnPostsFromRepository() {
        List<Post> posts = List.of(mock(Post.class), mock(Post.class));
        when(postRepository.findByAuthorIdNotOrderByCreationDateDesc(1L)).thenReturn(posts);

        List<Post> result = feedService.getFeedPosts(1L);

        assertEquals(posts, result);
    }

    @Test
    void getAllPosts_shouldReturnAllPosts() {
        List<Post> posts = List.of(mock(Post.class), mock(Post.class));
        when(postRepository.findAllByOrderByCreationDateDesc()).thenReturn(posts);

        List<Post> result = feedService.getAllPosts();

        assertEquals(posts, result);
    }

    @Test
    void getPost_shouldReturnPost_whenFound() {
        Post post = new Post("Post content", author);
        when(postRepository.findById(any())).thenReturn(Optional.of(post));

        Post result = feedService.getPost(10L);

        assertEquals(post, result);
    }

    @Test
    void getPost_shouldThrowException_whenNotFound() {
        when(postRepository.findById(any())).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> feedService.getPost(10L)
        );

        assertEquals("Post not found", ex.getMessage());
    }

    @Test
    void deletePost_shouldDeletePost_whenUserIsAuthor() {
        Post post = new Post("Post content", author);

        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(userRepository.findById(any())).thenReturn(Optional.of(author));

        feedService.deletePost(10L, 1L);

        verify(postRepository).delete(post);
    }

    @Test
    void deletePost_shouldThrowException_whenUserIsNotAuthor() {
        Post post = new Post("Post content", author);

        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(userRepository.findById(any())).thenReturn(Optional.of(otherUser));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> feedService.deletePost(10L, 2L)
        );

        assertEquals("User is not the author of the post", ex.getMessage());
        verify(postRepository, never()).delete(any());
    }

    @Test
    void getPostsByUserId_shouldReturnPostsForThatUser() {
        List<Post> posts = List.of(mock(Post.class));
        when(postRepository.findByAuthorId(1L)).thenReturn(posts);

        List<Post> result = feedService.getPostsByUserId(1L);

        assertEquals(posts, result);
    }

    @Test
    void likePost_shouldAddLike_whenUserHasNotLikedYet() {
        Post post = new Post("Post content", author);
        if (post.getLikes() == null) {
            post.setLikes(new HashSet<>());
        }

        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(userRepository.findById(any())).thenReturn(Optional.of(author));
        when(postRepository.save(post)).thenReturn(post);

        Post result = feedService.likePost(10L, 1L);

        assertTrue(result.getLikes().contains(author));
        verify(postRepository).save(post);
    }

    @Test
    void likePost_shouldRemoveLike_whenUserAlreadyLiked() {
        Post post = new Post("Post content", author);
        HashSet<AuthenticationUser> likes = new HashSet<>();
        likes.add(author);
        post.setLikes(likes);

        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(userRepository.findById(any())).thenReturn(Optional.of(author));
        when(postRepository.save(post)).thenReturn(post);

        Post result = feedService.likePost(10L, 1L);

        assertFalse(result.getLikes().contains(author));
        verify(postRepository).save(post);
    }

    @Test
    void addComment_shouldSaveComment_whenPostAndUserExist() {
        Post post = new Post("Post content", author);

        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(userRepository.findById(any())).thenReturn(Optional.of(author));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Comment result = feedService.addComment(10L, 1L, "Nice post");

        assertEquals("Nice post", result.getContent());
        assertEquals(author, result.getAuthor());
        assertEquals(post, result.getPost());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void editComment_shouldUpdateAndSave_whenUserIsAuthor() {
        Comment comment = new Comment(new Post("Post content", author), author, "Old comment");

        when(commentRepository.findById(any())).thenReturn(Optional.of(comment));
        when(userRepository.findById(any())).thenReturn(Optional.of(author));
        when(commentRepository.save(comment)).thenReturn(comment);

        Comment result = feedService.editComment(5L, 1L, "Updated comment");

        assertEquals("Updated comment", result.getContent());
        verify(commentRepository).save(comment);
    }

    @Test
    void editComment_shouldThrowException_whenUserIsNotAuthor() {
        Comment comment = new Comment(new Post("Post content", author), author, "Old comment");

        when(commentRepository.findById(any())).thenReturn(Optional.of(comment));
        when(userRepository.findById(any())).thenReturn(Optional.of(otherUser));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> feedService.editComment(5L, 2L, "Updated comment")
        );

        assertEquals("User is not the author of the comment", ex.getMessage());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void deleteComment_shouldDeleteComment_whenUserIsAuthor() {
        Comment comment = new Comment(new Post("Post content", author), author, "Comment");

        when(commentRepository.findById(any())).thenReturn(Optional.of(comment));
        when(userRepository.findById(any())).thenReturn(Optional.of(author));

        feedService.deleteComment(5L, 1L);

        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteComment_shouldThrowException_whenUserIsNotAuthor() {
        Comment comment = new Comment(new Post("Post content", author), author, "Comment");

        when(commentRepository.findById(any())).thenReturn(Optional.of(comment));
        when(userRepository.findById(any())).thenReturn(Optional.of(otherUser));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> feedService.deleteComment(5L, 2L)
        );

        assertEquals("User is not the author of the comment", ex.getMessage());
        verify(commentRepository, never()).delete(any());
    }
}