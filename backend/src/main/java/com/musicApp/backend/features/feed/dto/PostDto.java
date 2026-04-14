/**
 * Class Name: PostDto
 * Date: February 13, 2026
 * @author Jose Bastidas
 *
 * Description:
 * This class represents the data transfer object used for posts.
 * It stores the text content of a post and an optional picture
 * associated with the post.
 */
package com.musicApp.backend.features.feed.dto;

public class PostDto {
    private String content;
    private String picture = null;

    /**
     * Creates an empty PostDto object.
     */
    public PostDto() {
    }

    /**
     * Returns the content of the post.
     *
     * @return the text content of the post
     */
    public String getContent() {
        return content;
    }

    /**
     * Updates the content of the post.
     *
     * @param content the new text content of the post
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Returns the picture associated with the post.
     *
     * @return the picture value for the post
     */
    public String getPicture() {
        return picture;
    }

    /**
     * Updates the picture associated with the post.
     *
     * @param picture the new picture value for the post
     */
    public void setPicture(String picture) {
        this.picture = picture;
    }
}