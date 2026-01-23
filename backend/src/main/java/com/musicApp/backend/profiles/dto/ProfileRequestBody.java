package com.musicApp.backend.profiles.dto;

public class ProfileRequestBody {
  private String fname;
  private String lname;
  private String email;
  private String password;
  private String bio;
  private String favorites;

public ProfileRequestBody(String fname, String lname, String email, String password, String bio, String favorites){
  this.fname = fname;
  this.lname = lname;
  this.email = email;
  this.password = password;
  this.bio = bio;
  this.favorites = favorites;
}

public ProfileRequestBody(){

}



}
