/**
 * Class Name: AuthenticationResponseBody
 * Package: com.musicApp.backend.features.authentication.dto
 * Date: November 10, 2025
 * @author Jose Bastidas
 *

 * Important Functions:
 * - Getter methods:
 *       Provide read-only access to private fields, since all fields are declared `final`.
 *
 * Data Structures:
 * - Primitive wrapper types (`String`, `Boolean`) are used for flexibility and null safety.
 * - Immutable object design: all fields are `final`, ensuring thread safety and data consistency.
 *
 * Algorithms:
 * - None. This class is purely a data container used to transfer response data.
 *   The immutability design pattern is used intentionally to prevent data mutation once constructed.
 */

package com.musicApp.backend.features.authentication.dto;

/**
 * This Data Transfer Object (DTO) represents the server’s response to user authentication
 * requests, including registration and login actions. It encapsulates essential response
 * data such as the authentication token (JWT), confirmation message, username, email,
 * and current login status.<br>
 *
 * This object is sent back to the frontend after a successful or failed authentication
 * operation, allowing the client to handle session storage, display feedback, and update
 * the user interface accordingly.
 */
public class AuthenticationResponseBody {
    private final String token;
    private final String message;
    private final String username;
    private final String email;
    private final Boolean loginStatus;

    /**
     * Used for simple responses containing only a token and message.
     * @param token
     * @param message
     */
    public AuthenticationResponseBody(String token, String message) {
        this.token = token;
        this.message = message;
        this.username = null;
        this.email = null;
        this.loginStatus = null;
    }

    /**
     * Used for more detailed responses including user information and login state.
     * @param token
     * @param message
     * @param username
     * @param email
     * @param loginStatus
     */
   public AuthenticationResponseBody(String token, String message, String username, String email, Boolean loginStatus) {
        this.token = token;
        this.message = message;
        this.username = username;
        this.email = email;
        this.loginStatus = loginStatus;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getLoginStatus() {
        return loginStatus;
    }
}