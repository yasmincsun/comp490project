/**
 * Class Name: AuthenticationRequestBody
 * Package: com.musicApp.backend.features.authentication.dto
 * Date: November 10, 2025
 * @author Jose Bastidas
 *
 * Important Functions:
 * - Getter and Setter methods for each field, allowing encapsulated access to user data.
 * - Validation: `@NotBlank` ensures that required fields are not null or empty.
 *
 * Data Structures:
 * - Basic data type fields: `String fname`, `String lname`, `String username`, `String email`, and `String password`.
 *   These store user information used for authentication and registration.
 *
 * Algorithms:
 * - No algorithmic logic is used here since this class serves purely as a data container (DTO).
 *   The validation annotations use Jakarta Bean Validation (JSR-380) under the hood to enforce constraints.
 */

package com.musicApp.backend.features.authentication.dto;

import jakarta.validation.constraints.NotBlank;


/**
 * This Data Transfer Object (DTO) is used for carrying user authentication data between
 * the frontend client and the backend authentication service. It encapsulates all
 * necessary user input fields for both registration and login requests.<br>
 *
 * The class also includes validation annotations to ensure that mandatory fields,
 * such as email and password, are provided in incoming requests.
 */
public class AuthenticationRequestBody {

    private String fname;
    private String lname;

    private String username;

    @NotBlank(message = "Email is mandatory.")
    private String email;
    @NotBlank(message = "Password is mandatory.")
    private String password;

        /**
     * Constructs an {@code AuthenticationRequestBody} object with the given user details.
     *
     * @param fname    the user's first name
     * @param lname    the user's last name
     * @param username the username chosen by the user
     * @param email    the user's email address (used for login)
     * @param password the user's plaintext password (validated before encoding)
     */
    public AuthenticationRequestBody(String fname, String lname, String username, String email, String password) {
        this.fname = fname;
        this.lname = lname;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    /**
     * Gets the user's first name.
     *
     * @return the first name of the user
     */
    public String getName() {
        return fname;
    }

    /**
     * Sets the user's first name.
     *
     * @param fname the first name to assign to the user
     */
    public void setName(String fname) {
        this.fname = fname;
    }

    /**
     * Gets the user's last name.
     *
     * @return the last name of the user
     */
    public String getLastName(){
        return lname;
    }


    /**
     * Sets the user's last name.
     *
     * @param lname the last name to assign to the user
     */
    public void setLastName(String lname){
        this.lname = lname;
    }

    /**
     * Gets the user's username.
     *
     * @return the username associated with this authentication request
     */
    public String getUsername(){
        return username;
    }

    /**
     * Sets the user's username.
     *
     * @param username the username to assign to the user
     */ 
    public void setUsername(String username){
        this.username = username;
    }

    /**
     * Gets the user's email.
     *
     * @return the email address provided by the user
     */
        public String getEmail() {
            return email;
        }

            /**
     * Gets the user's password.
     *
     * @return the raw password entered by the user
     */
            public String getPassword() {
                return password;
            }

    /**
     * Sets the user's password.
     *
     * @param password the password to assign to the user
     */
                public void setPassword(String password) {
                    this.password = password;
        }
}
