package com.mychat.chatapp.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mychat.chatapp.dto.RegisterRequest;
import com.mychat.chatapp.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request){
        try {
            userService.register(request);
            return ResponseEntity.ok("User registered succesfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error in register " + e.getMessage());
        }
    }

}
