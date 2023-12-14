package com.feedverse.userservices.dto;

import java.io.Serializable;

import com.feedverse.userservices.model.DBUser;

public class UserDetailsDTO implements Serializable {
    private String username;
    private String name;
    private int followingCount;
    private int followersCount;

    public UserDetailsDTO() {
        // Default constructor
    }

    // You can add an additional constructor to directly initialize the DTO from a
    // DBUser entity
    public UserDetailsDTO(DBUser user) {
        this.username = user.getUsername();
        this.name = user.getName();
        this.followingCount = user.getFollowing().size();
        this.followersCount = user.getFollowers().size();
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    @Override
    public String toString() {
        return "UserDetailsDTO{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", followingCount=" + followingCount +
                ", followersCount=" + followersCount +
                '}';
    }

}
