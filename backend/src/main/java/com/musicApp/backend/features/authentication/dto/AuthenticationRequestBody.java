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

    public AuthenticationRequestBody(String fname, String lname, String username, String email, String password) {
        this.fname = fname;
        this.lname = lname;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return fname;
    }
    public void setName(String fname) {
        this.fname = fname;
    }

    public String getLastName(){
        return lname;
    }

    public void setLastName(String lname){
        this.lname = lname;
    }

    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }

        public String getEmail() {
            return email;
        }

            public String getPassword() {
                return password;
            }

                public void setPassword(String password) {
                    this.password = password;
        }
}
