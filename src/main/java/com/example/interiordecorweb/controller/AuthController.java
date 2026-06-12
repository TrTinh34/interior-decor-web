package com.example.interiordecorweb.controller;

import com.example.interiordecorweb.dto.RegisterDto;
import com.example.interiordecorweb.entity.Category;
import com.example.interiordecorweb.entity.Product;
import com.example.interiordecorweb.service.CategoryService;
import com.example.interiordecorweb.service.ProductService;
import com.example.interiordecorweb.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class AuthController {

    @Autowired private UserService userService;
    @Autowired private ProductService productService;
    @Autowired private CategoryService categoryService; // Đã thêm để sửa lỗi dòng 28, 29

    @GetMapping("/")
    public String indexPage(Model model) {

        // 1. Lấy tất cả danh mục hiển thị lên mục "Danh mục nổi bật"
        if (categoryService != null) {
            model.addAttribute("categories", categoryService.getAllCategories());
        }

        // 2. Lấy danh sách sản phẩm hoạt động gán vào "Sản phẩm nổi bật"
        // Sửa chữ P viết hoa thành p viết thường để gọi qua đối tượng đã @Autowired
        List<Product> activeProducts = productService.getAllActiveProductsList();
        model.addAttribute("featuredProducts", activeProducts);

        // 3. Gán tiếp vào mục "Tất cả sản phẩm" phía bên dưới
        model.addAttribute("allProducts", activeProducts);

        return "home/index";
    }


    @GetMapping("/login")
    public String loginPage() {
        
        return "auth/login";

    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterDto dto,
                           BindingResult result, Model model) {
        if (result.hasErrors()) return "auth/register";

        if (userService.existsByEmail(dto.getEmail())) {
            model.addAttribute("emailError", "Email đã tồn tại");
            return "auth/register";
        }
        userService.register(dto);
        return "redirect:/login?registered=true";
    }
}