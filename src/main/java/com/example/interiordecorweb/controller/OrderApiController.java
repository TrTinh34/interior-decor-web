package com.example.interiordecorweb.controller;

import com.example.interiordecorweb.entity.Order;
import com.example.interiordecorweb.entity.User;
import com.example.interiordecorweb.repository.OrderRepository;
import com.example.interiordecorweb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // ✅ ĐÃ SỬA: Thêm import để hết lỗi HttpStatus
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/orders")
public class OrderApiController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/history")
    public ResponseEntity<?> getOrderHistory(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Chưa đăng nhập");
        }

        try {
            // 1. Lấy email (Username) chính xác từ đối tượng auth bảo mật
            String email = auth.getName();

            // 2. Tìm đối tượng User từ database qua Email
            // ✅ ĐÃ SỬA: Thêm .orElse(null) để bóc tách dữ liệu từ Optional<User> ra User chuẩn
            User currentUser = userRepository.findByEmail(email).orElse(null);

            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy tài khoản người dùng");
            }

            Integer currentUserId = currentUser.getId();

            // 3. Gọi hàm lấy danh sách đơn hàng của UserId này
            List<Order> orders = orderRepository.findByUserIdOrderByOrderDateDesc(currentUserId);

            // 4. Chuyển đổi dữ liệu sang dạng Map phẳng tránh lỗi lặp tuần hoàn JSON khi trả về cho JS
            List<Map<String, Object>> result = orders.stream().map(order -> {
                Map<String, Object> orderMap = new HashMap<>();
                orderMap.put("id", order.getId());
                orderMap.put("orderDate", order.getOrderDate() != null ? order.getOrderDate().toString() : "");
                orderMap.put("totalAmount", order.getTotalAmount());
                orderMap.put("shippingAddress", order.getShippingAddress());
                orderMap.put("status", order.getStatus());

                List<Map<String, Object>> details = order.getOrderDetails().stream().map(detail -> {
                    Map<String, Object> detailMap = new HashMap<>();

                    if (detail.getProduct() != null) {
                        detailMap.put("productName", detail.getProduct().getName());
                        detailMap.put("productImage", detail.getProduct().getImageUrl());
                    } else {
                        detailMap.put("productName", "Sản phẩm không tồn tại");
                        detailMap.put("productImage", null);
                    }

                    detailMap.put("quantity", detail.getQuantity());
                    detailMap.put("unitPrice", detail.getUnitPrice());
                    return detailMap;
                }).collect(Collectors.toList());

                orderMap.put("items", details);
                return orderMap;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi xử lý hệ thống: " + e.getMessage());
        }
    }
}