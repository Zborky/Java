package com.kasino.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kasino.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
