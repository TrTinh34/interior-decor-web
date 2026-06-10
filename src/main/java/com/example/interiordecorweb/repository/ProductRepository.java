package com.example.interiordecorweb.repository;

import com.example.interiordecorweb.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Tìm kiếm + lọc + phân trang
    Page<Product> findByIsActiveTrueAndNameContainingIgnoreCase(
            String name, Pageable pageable);

    Page<Product> findByIsActiveTrueAndCategoryId(
            Integer categoryId, Pageable pageable);

    Page<Product> findByIsActiveTrueAndNameContainingIgnoreCaseAndCategoryId(
            String name, Integer categoryId, Pageable pageable);

    Page<Product> findByIsActiveTrue(Pageable pageable);
}
