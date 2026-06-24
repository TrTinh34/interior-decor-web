package com.example.interiordecorweb.controller;

import com.example.interiordecorweb.dto.RegisterDto;
import com.example.interiordecorweb.entity.Cart;
import com.example.interiordecorweb.entity.Product;
import com.example.interiordecorweb.service.CartService; // Đã thêm Import
import com.example.interiordecorweb.service.CategoryService;
import com.example.interiordecorweb.service.ProductService;
import com.example.interiordecorweb.service.UserService;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession; // Đã thêm Import

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication; // Đã thêm Import
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
    @Autowired private CategoryService categoryService;
    @Autowired private CartService cartService; // Đã thêm để lấy dữ liệu giỏ hàng

    @GetMapping("/")
    public String indexPage(Model model, Authentication auth, HttpSession session) {

        // ==========================================================================
        // LOGIC CẬP NHẬT SỐ LƯỢNG GIỎ HÀNG LÊN HEADER (Giữ nguyên đoạn này của bạn)
        // ==========================================================================
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Cart cart = cartService.getCartByEmail(auth.getName());
            int totalItems = (cart != null && cart.getCartItems() != null)
                    ? cart.getCartItems().stream().mapToInt(item -> item.getQuantity()).sum()
                    : 0;
            session.setAttribute("cartCount", totalItems);
        } else {
            session.setAttribute("cartCount", 0);
        }
        // ==========================================================================

        // 1. Lấy tất cả danh mục hiển thị lên mục "Danh mục nổi bật"
        if (categoryService != null) {
            model.addAttribute("categories", categoryService.getAllCategories());
        }

        // ĐÃ SỬA: Lấy danh sách gốc của tất cả sản phẩm đang kích hoạt
        List<Product> activeProducts = productService.getAllActiveProductsList();

        // 2. CHỈ LẤY TỐI ĐA 8 SẢN PHẨM ĐẦU TIÊN để gán vào "Sản phẩm nổi bật"
        List<Product> featuredTop8 = activeProducts.stream()
                .limit(8)
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("featuredProducts", featuredTop8);

        // 3. Khối "Tất cả sản phẩm"
        List<Product> allProductsTop8 = activeProducts.stream()
                .skip(8)
                .limit(8)
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("allProducts", allProductsTop8);

        // ĐÃ ĐỒNG BỘ: Chuyển hướng trả về đúng trang "home" thay vì "home/index"
        // (Vì file HTML bạn gửi ở lượt trước nằm ngay thư mục templates với tên home.html)
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