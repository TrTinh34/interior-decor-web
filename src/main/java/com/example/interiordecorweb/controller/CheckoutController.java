package com.example.interiordecorweb.controller;

import com.example.interiordecorweb.entity.User;
import com.example.interiordecorweb.repository.UserRepository;
import com.example.interiordecorweb.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Controller
public class CheckoutController {

    @Autowired private OrderService orderService;
    @Autowired private UserRepository userRepository;

    @GetMapping("/checkout")
    public String checkoutPage(Authentication auth, Model model) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("user", user);
        return "cart/checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@RequestParam String shippingAddress,
                             Authentication auth) {
        orderService.checkout(auth.getName(), shippingAddress);
        return "redirect:/orders/success";
    }

    @GetMapping("/orders/success")
    public String success() { return "cart/order-success"; }
}
