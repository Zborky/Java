package com.mychat.chatapp.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mychat.chatapp.dto.RegisterRequest;
import com.mychat.chatapp.model.User;
import com.mychat.chatapp.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection of UserRepository and PasswordEncoder
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public void register(RegisterRequest request){
        // Check if username already exists in the database
        if(userRepository.existsByUsername(request.getUsername())){
            throw new RuntimeException("Username already taken");
        }

        // Create a new User entity and set its properties
        User user = new User();
        user.setUsername(request.getUsername());
        // Encrypt the password before saving it
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());

        try{
            // Save the new user to the database
            userRepository.save(user);
        }catch(Exception e){
            // Catch any error during save and log it
            System.err.println("Error in save User " + e.getMessage());
            throw new RuntimeException("Error in register user");
        }

    }
}
