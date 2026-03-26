package com.musicApp.backend.features.feed.repository;
import com.musicApp.backend.features.feed.model.Post;

import java.util.List;

import org.springframework.data.jpa.repository.*;

public interface PostRepository extends JpaRepository<Post, Long> {
  List<Post> findByAuthorIdNotOrderByCreationDateDesc(Long authenticatedUserId);
  
  List<Post> findAllByOrderByCreationDateDesc();

  List<Post> findByAuthorId(Long userId);
}
