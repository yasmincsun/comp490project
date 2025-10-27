package com.musicApp.backend.features.authentication.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthenticationRequestBody {

    private String fname;
    private String lname;

    private String userName;

    @NotBlank(message = "Email is mandatory.")
    private String email;
    @NotBlank(message = "Password is mandatory.")
    private String password;

    public AuthenticationRequestBody(String fname, String lname, String userName, String email, String password) {
        this.fname = fname;
        this.lname = lname;
        this.userName = userName;
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
        return userName;
    }
    public void setUsername(String userName){
        this.userName = userName;
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
