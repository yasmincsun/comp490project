/**
 * Class Name: AuthenticationUser
 * Package: com.musicApp.backend.features.authentication.model
 * Date: November 10, 2025
 * @author Jose Bastidas
 *
 *
 * Important Functions:
 * - Standard getters and setters for all fields.
 * - getEmail(), getUsername(), getPassword(), getLoginStatus(), etc.
 * - JPA-managed relationships:
 *     - playlists: List<Playlist> owned by user
 *     - reviews: List<Review> created by user
 *     - currentMood: Mood entity associated with the user
 *
 * Data Structures:
 * - Basic fields: Strings for username, email, password, first/last name
 * - Boolean flags: emailVerified, loginStatus
 * - Tokens and expiry dates: String emailVerificationToken, passwordResetToken; LocalDateTime expiry dates
 * - Counters: int friends_count, playlist_count
 * - Relationships: List<Playlist>, List<Review>, Mood
 *
 * Algorithms / Design Decisions:
 * - Immutability is not strictly enforced due to JPA requirement for proxy objects.
 * - Token and expiry fields allow time-based verification for email confirmation
 *   and password reset workflows.
 * - CascadeType.ALL and orphanRemoval = true ensure that related playlists and
 *   reviews are automatically deleted when a user is deleted, maintaining referential integrity.
 * - Email validation (@Email) and NotNull constraints for username enforce data integrity
 *   at the database level.
 * - loginStatus boolean tracks online presence of a user in real time (updated via service layer).

 */

package com.musicApp.backend.features.authentication.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

import com.musicApp.backend.features.databasemodel.*;;

/**
 * This class represents the user entity for the MusicApp backend and maps to the
 * "users" table in the database. It stores all relevant user information, including
 * authentication credentials, account status, social data (friends, playlists), and
 * mood tracking. <br>
 * 
 * It is annotated as a JPA Entity and integrates with Hibernate for ORM.<br>
 * Relationships:<br>
 * - Many-to-one relationship with Mood (current mood of user)<br>
 * - One-to-many relationship with Playlist (user-owned playlists)<br>
 * - One-to-many relationship with Review (reviews written by user)
 */
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
    private Boolean loginStatus = false; //find a way to change when someone is online
    private int playlist_count;
    private int friends_count;

    @ManyToOne
    @JoinColumn(name = "mood_id")
    private Mood currentMood;

    // Playlists owned by this user (mapped by username)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Playlist> playlists;

     // Reviews written by this user
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
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

    public boolean isLoginStatus(){
        return loginStatus;
    }

    public void setLoginStatus(Boolean loginStatus){
        this.loginStatus = loginStatus;
    }

    public void setCurrentMood(Mood currentMood) {
        this.currentMood = currentMood;  
    }

    public Mood getCurrentMood() {
         return currentMood;
    }

}
