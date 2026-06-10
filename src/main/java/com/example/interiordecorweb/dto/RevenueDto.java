package com.example.interiordecorweb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class RevenueDto {
    private BigDecimal dailyRevenue;
    private BigDecimal monthlyRevenue;
    private BigDecimal yearlyRevenue;
    private List<String> chartLabels;
    private List<BigDecimal> chartData;
}