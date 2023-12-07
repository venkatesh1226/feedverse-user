package com.feedverse.userservices.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feedverse.userservices.model.DBUser;
import com.feedverse.userservices.dto.UserDetailsDTO;
import com.feedverse.userservices.model.Connection;
import com.feedverse.userservices.repository.ConnectionRepository;
import com.feedverse.userservices.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ConnectionRepository connectionRepository;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper; // Jackson's ObjectMapper

    public void updateFollow(String currentUserId, String followedUserId, String topicName) {
        Map<String, String> followEvent = new HashMap<>();
        followEvent.put("currentUserId", currentUserId);
        followEvent.put("followedUserId", followedUserId);

        try {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("UserName", currentUserId);
            map.put("FollowerName", followedUserId);
            String msg = ((new ObjectMapper()).writeValueAsString(map));
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topicName, msg);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println("Sent message=[" + msg +
                            "] with offset=[" + result.getRecordMetadata().offset() + "]");
                } else {
                    System.out.println("Unable to send message=[" +
                            msg + "] due to : " + ex.getMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // public String getCurrentUsername() {
    // Authentication authentication =
    // SecurityContextHolder.getContext().getAuthentication();

    // if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
    // Jwt jwt = (Jwt) authentication.getPrincipal();
    // return jwt.getClaimAsString("preferred_username");
    // }
    // return null;
    // }

    public DBUser getUserById(String username) {
        return userRepository.findUsersByExactUsernameOrEmail(username).stream().findFirst().orElse(null);
    }

    public List<DBUser> getAllUsers() {
        return userRepository.findAll();
    }

    public DBUser deleteUser(String username, String curUser) {
        DBUser user = userRepository.findById(username).orElse(null);
        if (user.getRole().equalsIgnoreCase("admin") || curUser.equalsIgnoreCase(username)) {
            userRepository.deleteById(username);
            return user;
        }
        throw new RuntimeException("You are not authorized to delete this user");
    }

    public DBUser updateUser(DBUser user, String curUser) {
        if (curUser.equalsIgnoreCase(user.getUsername())) {
            DBUser existingUser = userRepository.findById(user.getUsername()).orElse(null);

            existingUser.setName(user.getName());
            return userRepository.save(existingUser);
        }
        throw new RuntimeException("You are not authorized to update this user");
    }

    public Set<UserDetailsDTO> getFollowers(String username) {
        DBUser user = getUserById(username);
        System.out.println(user);
        if (user == null) {
            throw new RuntimeException("User not found");
        } else
            return user.getDetailedFollowers();
    }

    public Set<UserDetailsDTO> getFollowing(String username) {
        DBUser user = userRepository.findById(username).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return user.getDetailedFollowing();
    }

    @Transactional
    public void followUser(String username, String usernameToFollow)
            throws IllegalArgumentException {
        DBUser follower = userRepository.findByUsername(username);
        DBUser followed = userRepository.findByUsername(usernameToFollow);

        if (follower == null || followed == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (!connectionRepository.existsBySourceAndTarget(follower, followed)) {
            Connection connection = new Connection(follower, followed, LocalDate.now());
            connectionRepository.save(connection);

            follower.follow(connection);
            followed.addFollower(connection);

            userRepository.save(follower);
            userRepository.save(followed);
        } else {
            throw new IllegalArgumentException("User is already following");
        }
    }

    // @Transactional
    // public DBUser followUser(String username, String usernameToFollow) {
    // userRepository.followUser(username, usernameToFollow);
    // // updateFollow(username, usernameToFollow, "userFollowTopic");
    // return userRepository.findByUsername(username);
    // // DBUser user= userRepository.findById(username).orElse(null);
    // // DBUser userToFollow=
    // userRepository.findById(usernameToFollow).orElse(null);
    // // Follows follows = new Follows(userToFollow, java.time.LocalDate.now());
    // // Follows followers=new Follows(user, follows.getSince());
    // // user.getFollowing().add(follows);
    // // userToFollow.getFollowers().add(followers);
    // // userRepository.save(user);
    // // userRepository.save(userToFollow);
    // // return user;
    // }

    public Integer getFollowerCount(String username) {
        DBUser user = userRepository.findById(username).orElse(null);
        return user.getFollowers().size();
    }

    public Integer getFollowingCount(String username) {
        DBUser user = userRepository.findById(username).orElse(null);
        return user.getFollowing().size();
    }

    @Transactional
    public void unfollowUser(String username, String usernameToUnfollow) {
        DBUser follower = userRepository.findByUsername(username);
        DBUser followed = userRepository.findByUsername(usernameToUnfollow);

        if (follower == null || followed == null) {
            throw new IllegalArgumentException("User not found");
        }

        Connection connection = connectionRepository.findBySourceAndTarget(follower, followed);
        if (connection == null) {
            throw new IllegalArgumentException("Follow relationship does not exist");
        }

        follower.unfollow(connection);
        followed.removeFollower(connection);
        connectionRepository.delete(connection);

        userRepository.save(follower);
        userRepository.save(followed);
    }

    // public DBUser unfollowUser(String username, String usernameToUnfollow) {

    // userRepository.unfollowUser(username, usernameToUnfollow);
    // // updateFollow(username, usernameToUnfollow, "userFollowTopic");
    // Optional<DBUser> user = userRepository.findById(username);

    // return user.orElse(null);
    // //
    // // DBUser user= userRepos
    // // DBUser userToUnfollow= userRepositor
    // // ows follows =null;
    // // lows f: us
    // // f.getT
    // //
    // //
    // // }
    // //
    // // ollows!=null){
    // // Set<Follows> following=use
    // // Set<Follows> followers=use
    // // following.remove(follows);
    // // followers.remove(follows);
    // // userToUnfollow.setFollowers(follower
    // // user.setFollowing(followin
    // //
    // // userRepository.save(user);
    // // }
    // // throw new RuntimeException("You are not following this user");
    // }

    public Set<DBUser> getSuggestedFollowers(String username) {

        return userRepository.findNonMutualFollowers(username);

    }

    public Set<DBUser> getSearchUsers(String searchParam) {
        return userRepository.findUsers(searchParam);
    }
}
