package com.musicApp.backend.features.feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.musicApp.backend.features.feed.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;;

public interface CommentRepository extends JpaRepository<Comment, Long>{
  
} 
