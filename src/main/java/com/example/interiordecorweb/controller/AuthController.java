package com.example.interiordecorweb.controller;

import com.example.interiordecorweb.dto.RegisterDto;
import com.example.interiordecorweb.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
@Controller
public class AuthController {

    @Autowired private UserService userService;

    @GetMapping("/login")
    public String loginPage() { return "auth/login"; }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterDto dto,
                           BindingResult result, Model model) {
        if (result.hasErrors()) return "auth/register";

        if (userService.existsByEmail(dto.getEmail())) {
            model.addAttribute("emailError", "Email đã tồn tại");
            return "auth/register";
        }
        userService.register(dto);
        return "redirect:/login?registered=true";
    }
}