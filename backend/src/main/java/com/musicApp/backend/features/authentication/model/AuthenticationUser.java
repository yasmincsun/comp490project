package com.musicApp.backend.features.authentication.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

import com.musicApp.backend.features.databasemodel.*;;

// @Entity(name="users")
@Entity
@Table(name = "users")
public class AuthenticationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fname;
    private String lname;

    @NotNull
    @Column(unique = true)
    private String username;

    @Email
    private String email;
    
    @JsonIgnore
    private String password;
    private Boolean emailVerified = false;
    private String emailVerificationToken = null;
    private LocalDateTime emailVerificationTokenExpiryDate = null;
    private String passwordResetToken = null;
    private LocalDateTime passwordResetTokenExpiryDate = null;
    private Boolean login_status = false; //find a way to change when someone is online
    private int playlist_count;
    private int friends_count;

    @ManyToOne
    @JoinColumn(name = "mood_id")
    private Mood currentMood;

    // Playlists owned by this user (mapped by username)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Playlist> playlists;

     // Reviews written by this user
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Review> reviews;
    
    
    

    public AuthenticationUser(String fname, String lname, String username, String email, String password) {
        this.fname = fname;
        this.lname = lname;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public AuthenticationUser() {

    }

    public Long getId() {
        return id;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getEmail() {
        return email;
    }

    public String getEmailVerificationToken() {
        return emailVerificationToken;
    }

    public void setEmailVerificationToken(String emailVerificationToken) {
        this.emailVerificationToken = emailVerificationToken;
    }

    public LocalDateTime getEmailVerificationTokenExpiryDate() {
        return emailVerificationTokenExpiryDate;
    }

    public void setEmailVerificationTokenExpiryDate(LocalDateTime emailVerificationTokenExpiryDate) {
        this.emailVerificationTokenExpiryDate = emailVerificationTokenExpiryDate;
    }

    public String getPasswordResetToken() {
        return passwordResetToken;
    }

    public void setPasswordResetToken(String passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
    }

    public LocalDateTime getPasswordResetTokenExpiryDate() {
        return passwordResetTokenExpiryDate;
    }

    public void setPasswordResetTokenExpiryDate(LocalDateTime passwordResetTokenExpiryDate) {
        this.passwordResetTokenExpiryDate = passwordResetTokenExpiryDate;
    }

    public String getPassword() {
        return password;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return fname;
    }
    public void setName(String fname) {
        this.fname = fname;
    }

    public String getLastName() {
        return lname;
    }
    public void setLastName(String lname) {
        this.lname = lname;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public int getFriendCount(){
        return friends_count;
    }

    public int getPlaylistCount(){
        return playlist_count;
    }

    public boolean getStatus(){
        return login_status;
    }

    public void setCurrentMood(Mood currentMood) {
        this.currentMood = currentMood;  
    }

    public Mood getCurrentMood() {
         return currentMood;
    }

}
