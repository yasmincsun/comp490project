/**
 * Date: September 25, 2025
 * @author Jose Bastidas
 *
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
     * @param token   the JWT token generated upon successful authentication
     * @param message a textual message describing the result of the operation
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
     * @param token       the JWT token issued to the user
     * @param message     a message summarizing the authentication result
     * @param username    the username associated with the authenticated user
     * @param email       the email of the authenticated user
     * @param loginStatus indicates whether the authentication attempt was successful
     */
   public AuthenticationResponseBody(String token, String message, String username, String email, Boolean loginStatus) {
        this.token = token;
        this.message = message;
        this.username = username;
        this.email = email;
        this.loginStatus = loginStatus;
    }

    /**
     * Returns the JSON Web Token (JWT) associated with this authentication response.
     *
     * @return the JWT token, or {@code null} if not applicable
     */
    public String getToken() {
        return token;
    }

    /**
     * Returns the message describing the outcome of the authentication request.
     *
     * @return a textual message about the authentication result
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the username associated with the authenticated user.
     *
     * @return the username, or {@code null} if not included in the response
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the email address associated with the authenticated user.
     *
     * @return the user’s email, or {@code null} if not included in the response
     */
    public String getEmail() {
        return email;
    }

    /**
     * Indicates whether the user is successfully logged in.
     *
     * @return {@code true} if login succeeded, {@code false} otherwise;
     *         may be {@code null} for minimal responses
     */
    public Boolean getLoginStatus() {
        return loginStatus;
    }
}