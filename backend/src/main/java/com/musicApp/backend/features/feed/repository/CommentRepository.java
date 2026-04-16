/**
 * Class Name: CommentRepository
 * Date: February 13, 2026
 * @author Jose Bastidas
 *
 */
package com.musicApp.backend.features.feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.musicApp.backend.features.feed.model.Comment;


/**
 * This interface provides database access operations for Comment objects.
 * It extends JpaRepository so the application can perform standard
 * create, read, update, and delete operations on comments.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

}