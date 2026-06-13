package com.example.interiordecorweb.controller;

import com.example.interiordecorweb.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "admin/orders";
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam("orderId") Integer orderId,
                               @RequestParam("status") String status) {

        // 1. Thực thi lưu database thông qua service
        orderService.updateStatus(orderId, status);

        // 2. Chuyển hướng trình duyệt quay về trang danh sách (Tránh lỗi lưu lặp dữ liệu)
        return "redirect:/admin/orders";
    }
}
