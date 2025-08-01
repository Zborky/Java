package com.mychat.chatapp.service;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mychat.chatapp.dto.RegisterRequest;
import com.mychat.chatapp.dto.UpdateProfileRequest;
import com.mychat.chatapp.model.User;
import com.mychat.chatapp.repository.UserRepository;

/**
 * Service class responsible for user-related operations such as registration, profile updates, and retrieval.
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Default profile picture URL used if no custom picture is uploaded
    private final String defaultProfilePic = "https://example.com/images/default-profile.png";

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Registers a new user in the system.
     * 
     * @param request Object containing user information from registration form
     * @throws RuntimeException if username is already taken or saving fails
     */
    public void register(RegisterRequest request){
        // Check if username is already in use
        if(userRepository.existsByUsername(request.getUsername())){
            throw new RuntimeException("Username already taken");
        }

        // Create a new user and populate fields
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Encrypt password
        user.setEmail(request.getEmail());
        user.setAge(request.getAge());
        user.setNickName(request.getUsername()); // Default nickname same as username
        user.setProfilePictureUrl(defaultProfilePic); // Set default profile picture

        try {
            userRepository.save(user); // Save user to database
        } catch(Exception e) {
            System.err.println("Error saving user: " + e.getMessage());
            throw new RuntimeException("Error in register user");
        }
    }

    /**
     * Retrieves a user by their username.
     * 
     * @param username the username to search for
     * @return Optional containing user if found
     */
    public Optional<User> getUserByUserName(String username){
        return userRepository.findByUsername(username);
    }

    /**
     * Retrieves a user by their ID.
     * 
     * @param id the user ID to search for
     * @return Optional containing user if found
     */
    public Optional<User> getUserById(Long id){
        return userRepository.findById(id);
    }

    /**
     * Updates a user's profile fields such as nickname, age, and profile picture URL.
     * 
     * @param username the username of the user to update
     * @param profileRequest the updated profile information
     * @return the updated User object
     * @throws RuntimeException if user is not found
     */
    public User updateProfile(String username, UpdateProfileRequest profileRequest) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setNickName(profileRequest.getNickName());
            user.setAge(profileRequest.getAge());
            user.setProfilePictureUrl(profileRequest.getProfilePictureUrl());

            return userRepository.save(user); // Save updated user
        } else {
            throw new RuntimeException("User not found");
        }
    }

    /**
     * Updates a user's profile including uploading a new profile image to the local disk.
     * 
     * @param username the username of the user to update
     * @param nickName new nickname
     * @param age new age
     * @param file MultipartFile containing the new profile image
     * @return the updated User object
     * @throws RuntimeException if user is not found or file upload fails
     */
    public User updateProfileWithImage(String username, String nickName, int age, MultipartFile file) {
        // Retrieve user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Update basic user fields
        user.setNickName(nickName);
        user.setAge(age);

        // If a file is provided and not empty, save it
        if (file != null && !file.isEmpty()) {
            System.out.println("Uploading file: " + file.getOriginalFilename());
            try {
                String originalFilename = file.getOriginalFilename();
                String extension = "";

                // Extract file extension
                int dotIndex = originalFilename.lastIndexOf('.');
                if (dotIndex >= 0) {
                    extension = originalFilename.substring(dotIndex);
                }

                // Generate a unique filename
                String filename = UUID.randomUUID().toString() + extension;
                String uploadDir = "C:/Users/Jakub/Desktop/Java/chatapp/uploads";

                // Create file destination
                File dest = new File(uploadDir + File.separator + filename);
                dest.getParentFile().mkdirs(); // Ensure directory exists

                // Save file to disk
                file.transferTo(dest);

                // Set profile picture URL pointing to uploaded image
                String imageUrl = "http://localhost:8080/uploads/" + filename;
                user.setProfilePictureUrl(imageUrl);

                System.out.println("File uploaded to: " + dest.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to store file", e);
            }
        } else {
            System.out.println("No file uploaded");
        }

        return userRepository.save(user); // Save updated user
    }

    /**
     * Deletes a user from the system by their username.
     * 
     * @param username the username of the user to delete
     * @throws RuntimeException if user is not found
     */
    public void deleteUser(String username){
        Optional<User> userOpt = userRepository.findByUsername(username);
        if(userOpt.isPresent()){
            userRepository.delete(userOpt.get()); // Delete user if found
        } else {
            throw new RuntimeException("User not found");
        }
    }
}
