package com.example.interiordecorweb.service;
import com.example.interiordecorweb.dto.RevenueDto;
import com.example.interiordecorweb.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RevenueService {

    @Autowired private OrderRepository orderRepository;

    public RevenueDto getRevenue(LocalDate selectedDate) {
        int year = selectedDate.getYear();
        int month = selectedDate.getMonthValue();

        BigDecimal daily = orderRepository.sumRevenueByDate(selectedDate);
        BigDecimal monthly = orderRepository.sumRevenueByMonth(year, month);
        BigDecimal yearly = orderRepository.sumRevenueByYear(year);

        // 7 ngày kể từ selectedDate trở về trước
        LocalDateTime endDt = selectedDate.atTime(23, 59, 59);
        LocalDateTime startDt = selectedDate.minusDays(6).atStartOfDay();

        List<Object[]> rawData = orderRepository.revenueByDateRange(startDt, endDt);

        // Build map date -> revenue
        Map<LocalDate, BigDecimal> revenueMap = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            revenueMap.put(selectedDate.minusDays(i), BigDecimal.ZERO);
        }
        for (Object[] row : rawData) {
            LocalDate d = ((java.sql.Date) row[0]).toLocalDate();
            revenueMap.put(d, (BigDecimal) row[1]);
        }

        List<String> labels = revenueMap.keySet().stream()
                .map(LocalDate::toString).collect(Collectors.toList());
        List<BigDecimal> data = new ArrayList<>(revenueMap.values());

        return new RevenueDto(daily, monthly, yearly, labels, data);
    }
}
