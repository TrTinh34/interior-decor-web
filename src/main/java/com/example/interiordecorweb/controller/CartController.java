package com.example.interiordecorweb.controller;

import com.example.interiordecorweb.entity.Cart;
import com.example.interiordecorweb.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired private CartService cartService;



    @GetMapping
    public String viewCart(Authentication auth, Model model, HttpSession session) {
        Cart cart = cartService.getCartByEmail(auth.getName());
        model.addAttribute("cart", cart);
        model.addAttribute("total", cartService.getTotal(cart));

        // CẬP NHẬT TRẠNG THÁI: Tính tổng số lượng món đồ có trong giỏ hàng đưa vào session
        int totalItems = (cart != null && cart.getCartItems() != null)
                ? cart.getCartItems().stream().mapToInt(item -> item.getQuantity()).sum()
                : 0;
        session.setAttribute("cartCount", totalItems);

        return "cart/view";
    }

    @PostMapping("/add")
    public String addItem(@RequestParam Integer productId,
                          @RequestParam(defaultValue = "1") Integer quantity,
                          Authentication auth,
                          HttpSession session) {
        // 1. Thêm hàng vào DB thông qua service của bạn
        cartService.addItem(auth.getName(), productId, quantity);

        // 2. Lấy lại giỏ hàng mới nhất để tính toán lại tổng số lượng
        Cart cart = cartService.getCartByEmail(auth.getName());
        int totalItems = (cart != null && cart.getCartItems() != null)
                ? cart.getCartItems().stream().mapToInt(item -> item.getQuantity()).sum()
                : 0;

        // 3. Đè con số mới vào session để Header trang tiếp theo hiển thị ngay lập tức
        session.setAttribute("cartCount", totalItems);

        return "redirect:/product"; // Hoặc redirect:/product tùy bạn muốn chuyển hướng đi đâu
    }

    @PostMapping("/add-ajax")
    @ResponseBody // <--- BẮT BUỘC: Để Spring Boot trả về dữ liệu thuần JSON, không trả về trang HTML
    public ResponseEntity<?> addItemAjax(@RequestParam Integer productId,
                                         @RequestParam(defaultValue = "1") Integer quantity,
                                         Authentication auth,
                                         HttpSession session) {
        // Kiểm tra an toàn xem người dùng đăng nhập chưa
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            // Trả về mã lỗi 401 Unauthorized nếu chưa đăng nhập để JS bắt được và chuyển hướng sang /login
            return ResponseEntity.status(401).body("Chưa đăng nhập");
        }

        try {
            // 1. Thêm sản phẩm vào giỏ hàng thông qua cơ sở dữ liệu
            cartService.addItem(auth.getName(), productId, quantity);

            // 2. Tính toán tổng số lượng item mới nhất hiện tại trong giỏ
            Cart cart = cartService.getCartByEmail(auth.getName());
            int totalItems = (cart != null && cart.getCartItems() != null)
                    ? cart.getCartItems().stream().mapToInt(item -> item.getQuantity()).sum()
                    : 0;

            // 3. Đồng thời cập nhật luôn vào Session (để phòng hờ người dùng F5 hoặc đổi trang)
            session.setAttribute("cartCount", totalItems);

            // 4. Trả con số tổng số lượng về cho JavaScript xử lý tăng số lơ lửng trên Header
            return ResponseEntity.ok(totalItems);

        } catch (Exception e) {
            // Trả về mã lỗi 500 nếu có sự cố xảy ra ở Backend
            return ResponseEntity.status(500).body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @PostMapping("/update/{itemId}")
    public String updateItem(@PathVariable Integer itemId,
                             @RequestParam Integer quantity) {
        cartService.updateItem(itemId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/remove/{itemId}")
    public String removeItem(@PathVariable Integer itemId) {
        cartService.removeItem(itemId);
        return "redirect:/cart";
    }
}
