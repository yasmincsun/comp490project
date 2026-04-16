/**
 * Class Name: CommentDto
 * Date: February 13, 2026
 * @author Jose Bastidas
 *
 */
package com.musicApp.backend.features.feed.dto;

/**
 * This class represents the data transfer object used for comments.
 * It stores the content of a comment that is sent between the client
 * and the application.
 */
public class CommentDto {

    private String content;

    /**
     * Creates a CommentDto object with comment content.
     *
     * @param content the text content of the comment
     */
    public CommentDto(String content) {
        this.content = content;
    }

    /**
     * Creates an empty CommentDto object.
     */
    public CommentDto() {
    }

    /**
     * Returns the content of the comment.
     *
     * @return the comment text
     */
    public String getContent() {
        return content;
    }

    /**
     * Updates the content of the comment.
     *
     * @param content the new comment text
     */
    public void setContent(String content) {
        this.content = content;
    }
}