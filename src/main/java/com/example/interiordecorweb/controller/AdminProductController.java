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
import org.springframework.data.domain.Page;
@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    @Autowired private ProductService productService;
    @Autowired private CategoryRepository categoryRepository;

    @GetMapping
    public String list(@RequestParam(value = "page", defaultValue = "0") int page, Model model) {
        // 1. Lấy nguyên đối tượng Page (chứa cả data, tổng số trang, trang hiện tại)
        Page<Product> productPage = productService.getProducts(null, null, null, page);

        // 2. Đẩy đối tượng Page này sang bên View giao diện
        model.addAttribute("productPage", productPage);
        // 3. Đẩy danh sách sản phẩm để vòng lặp th:each cũ không bị lỗi
        model.addAttribute("products", productPage.getContent());

        return "admin/products";
    }

    @GetMapping("/create")
    public String newForm(Model model) {
        model.addAttribute("productDto", new ProductDto());
        model.addAttribute("categories", categoryRepository.findAll());
        // ✅ ĐÃ SỬA: Khớp với file templates/admin/product-form.html của bạn
        return "admin/product-form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        Product p = productService.findById(id);
        ProductDto dto = new ProductDto();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setPrice(p.getPrice());
        dto.setStock(p.getStock());
        dto.setDescription(p.getDescription());
        dto.setCategoryId(p.getCategory().getId());
        dto.setImageUrl(p.getImageUrl());

        model.addAttribute("productDto", dto);
        model.addAttribute("categories", categoryRepository.findAll());
        // ✅ ĐÃ SỬA: Khớp với file templates/admin/product-form.html của bạn
        return "admin/product-form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("productDto") ProductDto dto, // 🌟 BẮT BUỘC phải có tên định danh "productDto" ở đây để khớp với th:object bên HTML
                       BindingResult result,
                       Model model) {

        // 🛠️ BƯỚC 1: NẾU FORM NHẬP LIỆU BỊ LỖI VALIDATION
        if (result.hasErrors()) {
            // Nạp lại danh sách danh mục để thẻ <select> ngoài giao diện không bị trống rỗng
            model.addAttribute("categories", categoryRepository.findAll());
            // Trả về lại form để hiển thị thông báo lỗi đỏ
            return "admin/product-form";
        }

        // 🛠️ BƯỚC 2: TIẾN HÀNH LƯU VÀ BẮT LỖI GHI FILE (TỐI QUAN TRỌNG)
        try {
            productService.save(dto);
        } catch (IOException e) {
            e.printStackTrace(); // In lỗi ra màn hình console của IntelliJ để kiểm tra đường dẫn lưu ảnh nếu có lỗi

            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("errorMessage", "Lỗi hệ thống: Không thể lưu hình ảnh sản phẩm!");
            return "admin/product-form";
        }

        // 🛠️ BƯỚC 3: THÀNH CÔNG -> ĐIỀU HƯỚNG QUAY VỀ TRANG DANH SÁCH
        return "redirect:/admin/products";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        productService.softDelete(id);
        return "redirect:/admin/products";
    }
}