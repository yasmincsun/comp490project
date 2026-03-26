package com.musicApp.backend.features.authentication.dto;

import com.musicApp.backend.features.feed.model.Post;
import com.musicApp.backend.features.feed.repository.PostRepository;



public class CreatePostRequest {

    private String title;
    private String content;
    private String category;



    // Getters for title, content, and category
        public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getCategory() {
        return category;
    }


}
