package com.example.interiordecorweb.controller;

import com.example.interiordecorweb.entity.Product;
import com.example.interiordecorweb.repository.CategoryRepository;
import com.example.interiordecorweb.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired private ProductService productService;
    @Autowired private CategoryRepository categoryRepository;

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword,
                       @RequestParam(required = false) Integer categoryId,
                       @RequestParam(defaultValue = "") String sortBy,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {

        Page<Product> productPage = productService
                .getProducts(keyword, categoryId, sortBy, page);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("sortBy", sortBy);
        return "product/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        model.addAttribute("product", productService.findById(id));
        return "product/detail";
    }
}
