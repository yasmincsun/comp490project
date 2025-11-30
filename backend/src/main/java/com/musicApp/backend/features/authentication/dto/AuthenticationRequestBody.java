/**
 * Date: September 25, 2025
 * @author Jose Bastidas
 * 
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

    // No-arg constructor
    public AuthenticationRequestBody() {
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
     * Sets the user's email. Used for testing.
     *
     * @param email the email assigned to the user
     */
    public void setEmail(String email) {
        this.email = email;
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
