package com.example.interiordecorweb.controller;

import com.example.interiordecorweb.entity.Cart;
import com.example.interiordecorweb.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired private CartService cartService;

    @GetMapping
    public String viewCart(Authentication auth, Model model) {
        Cart cart = cartService.getCartByEmail(auth.getName());
        model.addAttribute("cart", cart);
        model.addAttribute("total", cartService.getTotal(cart));
        return "cart/view";
    }

    @PostMapping("/add")
    public String addItem(@RequestParam Integer productId,
                          @RequestParam(defaultValue = "1") Integer quantity,
                          Authentication auth) {
        cartService.addItem(auth.getName(), productId, quantity);
        return "redirect:/cart";
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
