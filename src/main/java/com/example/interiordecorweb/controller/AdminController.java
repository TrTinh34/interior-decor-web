package com.example.interiordecorweb.controller;

import com.example.interiordecorweb.entity.Order;
import com.example.interiordecorweb.repository.OrderRepository;
import com.example.interiordecorweb.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderService orderService;

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate selectedDate,
                            Model model) {
        // Nếu admin chưa chọn ngày cụ thể, mặc định lấy ngày hôm nay (Hiện tại là năm 2026)
        if (selectedDate == null) {
            selectedDate = LocalDate.now();
        }

        // 1. Tính toán số liệu doanh thu từ Repository
        BigDecimal dailyRevenue = orderRepository.sumRevenueByDate(selectedDate);
        BigDecimal monthlyRevenue = orderRepository.sumRevenueByMonth(selectedDate.getYear(), selectedDate.getMonthValue());
        BigDecimal yearlyRevenue = orderRepository.sumRevenueByYear(selectedDate.getYear());

        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("dailyRevenue", dailyRevenue);
        model.addAttribute("monthlyRevenue", monthlyRevenue);
        model.addAttribute("yearlyRevenue", yearlyRevenue);

        return "admin/dashboard";
    }

    // API phục vụ việc vẽ biểu đồ cột 7 ngày bằng AJAX / Chart.js
    @GetMapping("/api/revenue-chart")
    @ResponseBody
    public List<Map<String, Object>> getChartData(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate selectedDate) {
        LocalDateTime endDate = selectedDate.atTime(LocalTime.MAX);
        LocalDateTime startDate = selectedDate.minusDays(6).atStartOfDay();

        List<Object[]> queryResult = orderRepository.revenueByDateRange(startDate, endDate);

        // Tạo cấu trúc map dữ liệu gốc để bổ sung những ngày bị khuyết (doanh thu = 0)
        Map<LocalDate, BigDecimal> revenueMap = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            revenueMap.put(selectedDate.minusDays(i), BigDecimal.ZERO);
        }

        for (Object[] row : queryResult) {
            LocalDate date = java.sql.Date.valueOf(row[0].toString()).toLocalDate();
            BigDecimal revenue = (BigDecimal) row[1];
            revenueMap.put(date, revenue);
        }

        List<Map<String, Object>> chartData = new ArrayList<>();
        // Sắp xếp theo trình tự thời gian từ cũ tới mới
        revenueMap.keySet().stream().sorted().forEach(date -> {
            Map<String, Object> point = new HashMap<>();
            point.put("date", date.toString());
            point.put("revenue", revenueMap.get(date));
            chartData.add(point);
        });

        return chartData;
    }


}