package com.musicApp.backend.features.authentication.dto;

import jakarta.validation.constraints.NotBlank;

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
