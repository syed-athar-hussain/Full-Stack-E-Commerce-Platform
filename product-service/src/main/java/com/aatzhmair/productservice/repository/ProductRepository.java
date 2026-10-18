package com.aatzhmair.productservice.repository;

import com.aatzhmair.productservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {}
