package com.mychat.chatapp.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mychat.chatapp.model.User;
import com.mychat.chatapp.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository; 
    // Inject UserRepository to access user data from database

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        // Find user by username, throw exception if not found

        // We assume user.getRole() returns roles like "USER" or "ADMIN"
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            // Create a list with a single authority prefixed by "ROLE_"
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}
