package com.example.interiordecorweb.repository;

import com.example.interiordecorweb.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
