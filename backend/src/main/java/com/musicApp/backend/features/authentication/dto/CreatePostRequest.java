

/**
 * Date: March 22, 2026
 * @author Miguel Alfaro
 *
 */
package com.musicApp.backend.features.authentication.dto;

import com.musicApp.backend.features.feed.model.Post;
import com.musicApp.backend.features.feed.repository.PostRepository;



/**
 * DTO used for creating a new post.
 * Contains the basic fields required to submit post data from the client.
 */
public class CreatePostRequest {

    private String title;
    private String content;
    private String category;

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