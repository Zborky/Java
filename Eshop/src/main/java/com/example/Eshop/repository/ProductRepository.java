package com.example.Eshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Eshop.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}