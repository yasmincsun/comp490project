package com.musicApp.backend.features.feed.model;

// import org.springframework.data.annotation.Id;

import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotEmpty;
import jakarta.persistence.OneToMany;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
//import org.hibernate.mapping.OneToMany;

import com.musicApp.backend.features.authentication.model.AuthenticationUser;


@Entity(name = "posts")
public class Post {

    public Post(){

    }

    @Id
    private Long id;

    @NotEmpty
    private String content;
    private String picture;


    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private AuthenticationUser author;

    @CreationTimestamp
    private LocalDateTime creationDate;

    private LocalDateTime updatedDate;

    @OneToMany(
          mappedBy = "post",
          cascade = CascadeType.ALL, orphanRemoval = true
    )
    private List<Comment> comments;

    @ManyToMany
    @JoinTable(
        name = "posts_likes",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<AuthenticationUser> likes;

    @PreUpdate
    public void preUpdate(){
      this.updatedDate = LocalDateTime.now();
    }



    public Post(String content, AuthenticationUser author) {
      this.content = content;
      this.author = author;
    }

    public void setId(Long id){
      this.id = id;
    }

    public Long getId() {
      return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public AuthenticationUser getAuthor() {
      return author;
    }

    public void setAuthor(AuthenticationUser author) {
      this.author = author;
    }

    public String getPicture() {
      return picture;
    }

    public void setPicture(String picture) {
      this.picture = picture;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }


    public Set<AuthenticationUser> getLikes() {
        return likes;
    }

    public void setLikes(Set<AuthenticationUser> likes) {
        this.likes = likes;
    }
  
}
