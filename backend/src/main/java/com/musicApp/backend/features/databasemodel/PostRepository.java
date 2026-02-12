package com.musicApp.backend.features.databasemodel;

import org.springframework.data.jpa.repository.JpaRepository;



public interface PostRepository extends JpaRepository<Post, Long>{
	
}
