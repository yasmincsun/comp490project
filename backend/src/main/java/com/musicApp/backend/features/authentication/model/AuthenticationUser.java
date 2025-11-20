/**
 * Date: September 25, 2025
 * @author Jose Bastidas
 *
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
    
    
    
    /**
     * Constructs a new {@code AuthenticationUser} with basic identifying fields.
     *
     * @param fname user's first name
     * @param lname user's last name
     * @param username unique username chosen by the user
     * @param email user's email address
     * @param password user's hashed or raw password (depending on context)
     */
    public AuthenticationUser(String fname, String lname, String username, String email, String password) {
        this.fname = fname;
        this.lname = lname;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    /** Default constructor required by JPA. */
    public AuthenticationUser() {

    }

    /**
     * Gets user's ID.
     * @return unique ID of the user in the database
     */
    public Long getId() {
        return id;
    }

    /**
     * Reuturns if user's email is verified.
     * @return true if the user's email has been verified
     */
    public Boolean getEmailVerified() {
        return emailVerified;
    }

    /**
     * Sets the user's email verification status.
     * @param emailVerified true if email has been confirmed
     */
    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    /**
     * Gets user's email.
     * @return user's registered email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets user's email verification token.
     * @return token used for verifying the user's email
     */
    public String getEmailVerificationToken() {
        return emailVerificationToken;
    }

    /**
     * Sets the email verification token.
     * @param emailVerificationToken unique token for email verification
     */
    public void setEmailVerificationToken(String emailVerificationToken) {
        this.emailVerificationToken = emailVerificationToken;
    }

    /**
     * Gets user's email verification token expiration date.
     * @return date and time when the email verification token expires
     */
    public LocalDateTime getEmailVerificationTokenExpiryDate() {
        return emailVerificationTokenExpiryDate;
    }

    /**
     * Sets the expiration time of the email verification token.
     * @param emailVerificationTokenExpiryDate expiry timestamp
     */
    public void setEmailVerificationTokenExpiryDate(LocalDateTime emailVerificationTokenExpiryDate) {
        this.emailVerificationTokenExpiryDate = emailVerificationTokenExpiryDate;
    }

    /**
     * Gets the password reset token.
     * @return token used for resetting the user's password
     */
    public String getPasswordResetToken() {
        return passwordResetToken;
    }

    /**
     * Sets a new password reset token.
     * @param passwordResetToken unique token for password reset
     */
    public void setPasswordResetToken(String passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
    }

    /**
     * Gets the password reset token's expiration date.
     * @return timestamp for when the password reset token expires
     */
    public LocalDateTime getPasswordResetTokenExpiryDate() {
        return passwordResetTokenExpiryDate;
    }

    /**
     * Sets the expiration time for the password reset token.
     * @param passwordResetTokenExpiryDate expiration date and time
     */
    public void setPasswordResetTokenExpiryDate(LocalDateTime passwordResetTokenExpiryDate) {
        this.passwordResetTokenExpiryDate = passwordResetTokenExpiryDate;
    }

    /**
     * Gets user's password.
     * @return the user's password (hashed)
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's ID (used internally by JPA).
     * @param id unique user ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Sets the user's email address.
     * @param email valid email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Updates the user's password.
     * @param password new hashed password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets user's first name.
     * @return first name of the user
     */
    public String getName() {
        return fname;
    }

    /**
     * Sets the user's first name.
     * @param fname user's first name
     */
    public void setName(String fname) {
        this.fname = fname;
    }

    /**
     * Gets user's last name.
     * @return user's last name
     */
    public String getLastName() {
        return lname;
    }

    /**
     * Sets the user's last name.
     * @param lname user's last name
     */
    public void setLastName(String lname) {
        this.lname = lname;
    }

    /**
     * Gets username.
     * @return unique username associated with the user
     */
    public String getUsername(){
        return username;
    }

    /**
     * Sets the username for this user.
     * @param username unique username
     */
    public void setUsername(String username){
        this.username = username;
    }

    /**
     * Gets friend count.
     * @return total number of friends linked to this user
     */
    public int getFriendCount(){
        return friends_count;
    }

    /**
     * Gets playlist count.
     * @return total number of playlists created by the user
     */
    public int getPlaylistCount(){
        return playlist_count;
    }

    /**
     * Returns if user is logged in.
     * @return true if the user is currently logged in
     */
    public boolean isLoginStatus(){
        return loginStatus;
    }

    /**
     * Updates the user's login state.
     * @param loginStatus true if user is online
     */
    public void setLoginStatus(Boolean loginStatus){
        this.loginStatus = loginStatus;
    }

    /**
     * Assigns a new mood to the user.
     * @param currentMood {@link Mood} object representing current emotional state
     */
    public void setCurrentMood(Mood currentMood) {
        this.currentMood = currentMood;  
    }

    /**
     * Returns user's mood.
     * @return current {@link Mood} associated with the user
     */
    public Mood getCurrentMood() {
         return currentMood;
    }

}
