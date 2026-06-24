package com.example.interiordecorweb.service;

import com.example.interiordecorweb.entity.Category;
import com.example.interiordecorweb.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // Hàm lấy toàn bộ danh mục để hiển thị lên trang chủ
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}