-- Database: comp490_app
-- Unified schema for moods, users, songs, playlists, playlist_songs, and reviews (add any new tables here)
-- All foreign keys and relationships properly configured


DROP DATABASE IF EXISTS comp490_app;
CREATE DATABASE comp490_app;
USE comp490_app;

SET NAMES utf8mb4;
SET time_zone = '+00:00';
SET foreign_key_checks = 0;
SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';

-- Moods
CREATE TABLE moods (
  moodID INT NOT NULL AUTO_INCREMENT,
  major_mood VARCHAR(50) NOT NULL,
  sub_category VARCHAR(50),
  PRIMARY KEY (moodID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- Users (with foreign key to moods)

CREATE TABLE users (
  userID INT NOT NULL AUTO_INCREMENT,
  fname VARCHAR(50) NOT NULL,
  lname VARCHAR(50) NOT NULL,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(100) NOT NULL,
  phone_number VARCHAR(15),
  login_status TINYINT(1) DEFAULT 0,
  current_moodID INT,  
  playlist_count INT DEFAULT 0,
  friends_count INT DEFAULT 0,
  PRIMARY KEY (userID),
  UNIQUE KEY (username),
  UNIQUE KEY (email),
  KEY (current_moodID),
  CONSTRAINT fk_users_mood FOREIGN KEY (current_moodID)
    REFERENCES moods(moodID)
    ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- 3. songs (with foreign key to moods)

CREATE TABLE songs (
  songID INT NOT NULL AUTO_INCREMENT,
  title VARCHAR(100) NOT NULL,
  artist VARCHAR(100) NOT NULL,
  album VARCHAR(100),
  length TIME,
  release_date DATE,
  genre VARCHAR(50),
  moodID INT, 
  play_count INT DEFAULT 0,
  views INT DEFAULT 0,
  last_played DATETIME,
  PRIMARY KEY (songID),
  KEY (moodID),
  CONSTRAINT fk_songs_mood FOREIGN KEY (moodID)
    REFERENCES moods(moodID)
    ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- playlists (with foreign key to users)

CREATE TABLE playlists (
  playlistID INT NOT NULL AUTO_INCREMENT,
  username VARCHAR(50),
  title VARCHAR(100) NOT NULL,
  comment TEXT,
  privacy ENUM('public','private') DEFAULT 'public',
  runtime TIME,
  PRIMARY KEY (playlistID),
  KEY (username),
  CONSTRAINT fk_playlists_user FOREIGN KEY (username)
    REFERENCES users(username)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- playlist_songs

CREATE TABLE playlist_songs (
  playlistID INT NOT NULL,
  songID INT NOT NULL,
  PRIMARY KEY (playlistID, songID),
  KEY (songID),
  CONSTRAINT fk_playlist_songs_playlist FOREIGN KEY (playlistID)
    REFERENCES playlists(playlistID)
    ON DELETE CASCADE,
  CONSTRAINT fk_playlist_songs_song FOREIGN KEY (songID)
    REFERENCES songs(songID)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- reviews

CREATE TABLE reviews (
  reviewID INT NOT NULL AUTO_INCREMENT,
  username VARCHAR(50),
  songID INT,
  album VARCHAR(100),
  rating INT CHECK (rating BETWEEN 1 AND 5),
  date_posted DATE,
  comment TEXT,
  PRIMARY KEY (reviewID),
  KEY (username),
  KEY (songID),
  CONSTRAINT fk_reviews_user FOREIGN KEY (username)
    REFERENCES users(username)
    ON DELETE CASCADE,
  CONSTRAINT fk_reviews_song FOREIGN KEY (songID)
    REFERENCES songs(songID)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- Final setup commands

SET foreign_key_checks = 1;
