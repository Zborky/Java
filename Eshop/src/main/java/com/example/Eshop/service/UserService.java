package com.example.Eshop.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Eshop.dto.RegisterRequest;
import com.example.Eshop.model.User;
import com.example.Eshop.repository.UserRepository;
@Service

public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService (UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequest request){
        if(userRepository.existsByUsername(request.getUsername())){
            throw new RuntimeException("Username already taken");
        }

        //Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
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
