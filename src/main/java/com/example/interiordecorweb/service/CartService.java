package com.example.interiordecorweb.service;

import com.example.interiordecorweb.entity.*;
import com.example.interiordecorweb.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class CartService {

    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;

    public Cart getCartByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }

    public void addItem(String email, Integer productId, Integer quantity) {
        Cart cart = getCartByEmail(email);
        Product product = productRepository.findById(productId).orElseThrow();

        cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .ifPresentOrElse(
                        item -> item.setQuantity(item.getQuantity() + quantity),
                        () -> {
                            CartItem item = new CartItem();
                            item.setCart(cart);
                            item.setProduct(product);
                            item.setQuantity(quantity);
                            cartItemRepository.save(item);
                        }
                );
    }

    public void updateItem(Integer cartItemId, Integer quantity) {
        CartItem item = cartItemRepository.findById(cartItemId).orElseThrow();
        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
        }
    }

    public void removeItem(Integer cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    public BigDecimal getTotal(Cart cart) {
        return cart.getCartItems().stream()
                .map(i -> i.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}