package com.mychat.chatapp.controller;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mychat.chatapp.dto.UpdateProfileRequest;
import com.mychat.chatapp.dto.UserResponse;
import com.mychat.chatapp.model.User;
import com.mychat.chatapp.repository.UserRepository;
import com.mychat.chatapp.service.UserService;

import lombok.RequiredArgsConstructor;

/**
 * REST controller for handling user-related operations such as
 * fetching users, updating profiles, and uploading profile images.
 */
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Allow requests from any origin
@RequestMapping("/api/user") // Base path for user API
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;

    /**
     * GET /api/user/all
     * Returns a list of all users with basic public profile info.
     */
    @GetMapping("/all")
    public List<UserResponse> getAllUsers(){
        return userRepository.findAll().stream()
            .map(user -> new UserResponse(user.getNickName(), user.getAge(), user.getProfilePictureUrl()))
            .collect(Collectors.toList());
    }

    /**
     * POST /api/user/{username}/update
     * Updates the profile information of the user (without uploading a profile picture).
     *
     * @param username the username of the user to update
     * @param updateRequest the new profile information
     * @return the updated user object
     */
    @PostMapping("/{username}/update")
    public ResponseEntity<User> updateProfile(
        @PathVariable String username,
        @RequestBody UpdateProfileRequest updateRequest) {

        User updatedUser = userService.updateProfile(username, updateRequest);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * GET /api/user/{username}
     * Retrieves a user by their username.
     *
     * @param username the username to search for
     * @return the user if found, otherwise 404
     */
    @GetMapping("/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) {
        return userService.getUserByUserName(username)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/user/me
     * Retrieves the currently authenticated user.
     *
     * @param principal the security principal representing the current user
     * @return the current user's full profile
     */
    @GetMapping("/me") 
    public ResponseEntity<User> getCurrentUser(Principal principal) {
        String username = principal.getName(); // Get logged-in user's username
        return userService.getUserByUserName(username)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/user/{username}/upload
     * Updates a user's profile including uploading a profile picture.
     *
     * @param username the user to update
     * @param nickName new nickname to set
     * @param age new age to set
     * @param file image file to upload as profile picture
     * @return the updated user object
     */
    @PostMapping("/{username}/upload")
    public ResponseEntity<User> uploadProfilePicture(
        @PathVariable String username,
        @RequestParam("nickName") String nickName,
        @RequestParam("age") Integer age,
        @RequestParam("profileImage") MultipartFile file) {

        User updatedUser = userService.updateProfileWithImage(username, nickName, age, file);
        return ResponseEntity.ok(updatedUser);
    }

}
