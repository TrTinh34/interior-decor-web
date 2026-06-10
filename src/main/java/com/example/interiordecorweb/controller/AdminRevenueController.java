package com.example.interiordecorweb.controller;

import com.example.interiordecorweb.dto.RevenueDto;
import com.example.interiordecorweb.service.RevenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/revenue")
public class AdminRevenueController {

    @Autowired private RevenueService revenueService;

    @GetMapping
    public String revenuePage(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {

        if (date == null) date = LocalDate.now();

        RevenueDto revenue = revenueService.getRevenue(date);
        model.addAttribute("revenue", revenue);
        model.addAttribute("selectedDate", date);
        return "admin/revenue";
    }
}