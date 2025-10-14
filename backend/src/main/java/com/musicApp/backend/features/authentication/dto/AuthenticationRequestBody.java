package com.musicApp.backend.features.authentication.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthenticationRequestBody {

    private String name;

    @NotBlank(message = "Email is mandatory.")
    private String email;
    @NotBlank(message = "Password is mandatory.")
    private String password;

    public AuthenticationRequestBody(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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
