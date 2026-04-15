package com.musicApp.backend.features.friendship.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for Friendship using user1 and user2 identifiers.
 */
/**
 * Composite primary key class for Friendship.
 * Uses two user identifiers to uniquely identify a friendship record.
 * @author Yasmin Zubair
 * Date: April 15th, 2026
 */
public class FriendshipId implements Serializable {

    private Long user1;
    private Long user2;

    public FriendshipId() {
    }

    public FriendshipId(Long user1, Long user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    public Long getUser1() {
        return user1;
    }

    public void setUser1(Long user1) {
        this.user1 = user1;
    }

    public Long getUser2() {
        return user2;
    }

    public void setUser2(Long user2) {
        this.user2 = user2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendshipId that = (FriendshipId) o;
        return Objects.equals(user1, that.user1) && Objects.equals(user2, that.user2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user1, user2);
    }
}
