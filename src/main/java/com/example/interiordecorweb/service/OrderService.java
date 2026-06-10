package com.example.interiordecorweb.service;

import com.example.interiordecorweb.entity.*;
import com.example.interiordecorweb.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private UserRepository userRepository;

    public void checkout(String email, String shippingAddress) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow();

        if (cart.getCartItems().isEmpty())
            throw new RuntimeException("Giỏ hàng trống");

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setStatus("NEW");

        List<OrderDetail> details = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : cart.getCartItems()) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(item.getProduct().getPrice());
            details.add(detail);
            total = total.add(item.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        order.setTotalAmount(total);
        order.setOrderDetails(details);
        orderRepository.save(order);

        cartItemRepository.deleteAll(cart.getCartItems());
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll(Sort.by("orderDate").descending());
    }

    public void updateStatus(Integer orderId, String status) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus(status);
    }
}