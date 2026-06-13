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
@RequestMapping("/product") // 🎯 Đã cập nhật thành /product đúng yêu cầu của bạn
public class ProductController {

    @Autowired private ProductService productService;
    @Autowired private CategoryRepository categoryRepository;

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword,
                       @RequestParam(required = false) Integer categoryId,
                       @RequestParam(defaultValue = "") String sortBy,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {

        // Lấy dữ liệu phân trang từ Service (Mỗi trang gồm n sản phẩm tùy cấu hình Service của bạn)
        Page<Product> productPage = productService
                .getProducts(keyword, categoryId, sortBy, page);

        // Đẩy dữ liệu ra tầng giao diện Thymeleaf
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalElements", productPage.getTotalElements()); // Đếm tổng số sản phẩm tìm thấy
        model.addAttribute("currentPage", page);
        model.addAttribute("categories", categoryRepository.findAll()); // Danh sách danh mục cho sidebar

        // Giữ lại trạng thái các bộ lọc để hiển thị class active hoặc dùng cho phân trang
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("sortBy", sortBy);

        return "product/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);

        // Lấy thêm danh sách sản phẩm liên quan (Cùng CategoryId, giới hạn 4 sản phẩm để vừa khít màn hình)
        // Bạn có thể viết thêm 1 hàm đơn giản trong ProductService hoặc dùng tạm ProductRepository trực tiếp ở đây tùy cấu hình
        org.springframework.data.domain.Page<Product> relatedPage = productService
                .getProducts("", product.getCategory().getId(), "", 0); // Lấy trang đầu tiên của danh mục đó

        // Loại bỏ chính sản phẩm hiện tại ra khỏi danh sách gợi ý
        java.util.List<Product> relatedProducts = relatedPage.getContent().stream()
                .filter(p -> !p.getId().equals(id))
                .limit(4)
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("relatedProducts", relatedProducts);
        return "product/detail";
    }
}