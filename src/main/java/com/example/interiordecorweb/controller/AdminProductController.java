package com.example.interiordecorweb.controller;

import com.example.interiordecorweb.dto.ProductDto;
import com.example.interiordecorweb.entity.Product;
import com.example.interiordecorweb.repository.CategoryRepository;
import com.example.interiordecorweb.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    @Autowired private ProductService productService;
    @Autowired private CategoryRepository categoryRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("products",
                productService.getProducts(null, null, null, 0).getContent());
        return "admin/product/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("productDto", new ProductDto());
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/product/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        Product p = productService.findById(id);
        ProductDto dto = new ProductDto();
        dto.setId(p.getId()); dto.setName(p.getName());
        dto.setPrice(p.getPrice()); dto.setStock(p.getStock());
        dto.setDescription(p.getDescription());
        dto.setCategoryId(p.getCategory().getId());
        dto.setImageUrl(p.getImageUrl());
        model.addAttribute("productDto", dto);
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/product/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute ProductDto dto,
                       BindingResult result, Model model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "admin/product/form";
        }
        productService.save(dto);
        return "redirect:/admin/products";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        productService.softDelete(id);
        return "redirect:/admin/products";
    }
}