package com.kasino.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kasino.dto.RegisterRequest;
import com.kasino.model.User;
import com.kasino.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequest request) {
        // Check if user is exist
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        try {
            userRepository.save(user);
        } catch (Exception e) {
            // Catch error 
            System.err.println("Chyba pri ukladaní používateľa: " + e.getMessage());
            throw new RuntimeException("Chyba pri registrácii používateľa");
        }
    }
}
