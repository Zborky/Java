package com.example.Eshop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Eshop.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findTopByOrderByIdDesc();
    Optional<Order> findByOrderNumber(String orderNumber);
}
