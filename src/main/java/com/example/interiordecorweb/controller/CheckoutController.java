package com.example.interiordecorweb.controller;

import com.example.interiordecorweb.entity.Cart;
import com.example.interiordecorweb.entity.CartItem;
import com.example.interiordecorweb.entity.User;
import com.example.interiordecorweb.repository.UserRepository;
import com.example.interiordecorweb.service.CartService;
import com.example.interiordecorweb.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CheckoutController {

    @Autowired private OrderService orderService;
    @Autowired private UserRepository userRepository;
    @Autowired private CartService cartService;

    @GetMapping("/checkout")
    public String checkoutPage(Authentication auth, Model model) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("user", user);

        // Đổ danh sách sản phẩm giỏ hàng ra cột bên phải thiết kế Figma
        Cart cart = cartService.getCartByEmail(auth.getName());
        List<CartItem> cartItems = (cart != null) ? cart.getCartItems() : new ArrayList<>();
        model.addAttribute("cartItems", cartItems);

        // Tính tổng tiền đơn hàng để khách xem trước
        BigDecimal totalPrice = cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("totalPrice", totalPrice);

        return "cart/checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@RequestParam String fullName,
                             @RequestParam String phone,
                             @RequestParam String address,
                             @RequestParam(required = false, defaultValue = "") String note,
                             @RequestParam String paymentMethod,
                             Authentication auth) {

        // Gộp thông tin cá nhân thành chuỗi văn bản hoàn chỉnh để map vào trường shippingAddress trong Database của bạn
        String fullShippingInfo = String.format("Người nhận: %s | SĐT: %s | Địa chỉ: %s | Ghi chú: %s | HTTT: %s",
                fullName, phone, address, note, paymentMethod);

        orderService.checkout(auth.getName(), fullShippingInfo);
        return "redirect:/orders/success";
    }

    @GetMapping("/orders/success")
    public String success() {
        return "cart/order-success";
    }
}