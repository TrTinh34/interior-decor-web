package com.example.interiordecorweb.repository;

import com.example.interiordecorweb.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByCartIdAndProductId(Integer cartId, Integer productId);
    List<CartItem> findByCartId(Integer cartId);
}
