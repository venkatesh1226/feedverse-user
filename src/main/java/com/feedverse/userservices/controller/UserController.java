package com.feedverse.userservices.controller;

import com.feedverse.userservices.model.DBUser;
import com.feedverse.userservices.dto.UserDetailsDTO;
import com.feedverse.userservices.model.Connection;
import com.feedverse.userservices.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService; // Replace YourServiceClass with the actual name of your service class

    @GetMapping("/get/{username}")
    public DBUser getUserById(@PathVariable String username) {
        return userService.getUserById(username);
    }

    // @GetMapping("/get/current-user")
    // public DBUser getCurrentUser() {
    // return userService.getUserById(userService.getCurrentUsername());
    // }

    @GetMapping("/all")
    public List<DBUser> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/delete/{username}")
    public DBUser deleteUser(@PathVariable String username, @RequestParam String curUser) {
        return userService.deleteUser(username, curUser);
    }

    @PutMapping("/update")
    public DBUser updateUser(@RequestBody DBUser user, @RequestParam String curUser) {
        return userService.updateUser(user, curUser);
    }

    // return type is null for every Follow entity
    @GetMapping("/{username}/followers")
    public Set<UserDetailsDTO> getFollowers(@PathVariable String username) {
        return userService.getFollowers(username);
    }

    @GetMapping("/{username}/following")
    public Set<UserDetailsDTO> getFollowing(@PathVariable String username) {
        return userService.getFollowing(username);
    }

    @PostMapping("/{username}/follow/{usernameToFollow}")
    public ResponseEntity<?> followUser(@PathVariable String username, @PathVariable String usernameToFollow) {
        try {
            userService.followUser(username, usernameToFollow);
            return ResponseEntity.ok("Followed successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{username}/followerCount")
    public Integer getFollowerCount(@PathVariable String username) {
        return userService.getFollowerCount(username);
    }

    @GetMapping("/{username}/followingCount")
    public Integer getFollowingCount(@PathVariable String username) {
        return userService.getFollowingCount(username);
    }

    @PostMapping("/{username}/unfollow/{usernameToUnfollow}")
    public ResponseEntity<?> unfollowUser(@PathVariable String username, @PathVariable String usernameToUnfollow) {
        try {
            userService.unfollowUser(username, usernameToUnfollow);
            return ResponseEntity.ok().body("Unfollowed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/suggested/{username}")
    public Set<DBUser> getSuggestedFollowers(@PathVariable String username) {
        return userService.getSuggestedFollowers(username);
    }

    @GetMapping("/search")
    public Set<DBUser> getSearchUsers(@RequestParam String searchParam) {
        return userService.getSearchUsers(searchParam);
    }
}
