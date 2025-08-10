package com.example.Eshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Eshop.dto.RegisterRequest;
import com.example.Eshop.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") 

public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
    try {
        
        userService.register(request);
        return ResponseEntity.ok("User registered successfully");
    } catch (RuntimeException e) {
        
        return ResponseEntity.badRequest().body("Chyba pri registrácii: " + e.getMessage());
    }
}
}
