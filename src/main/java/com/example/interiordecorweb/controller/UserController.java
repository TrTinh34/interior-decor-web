package com.example.interiordecorweb.controller;

import com.example.interiordecorweb.entity.User;
import com.example.interiordecorweb.repository.UserRepository;
import com.example.interiordecorweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // 1. Hiển thị trang Hồ Sơ Cá Nhân với dữ liệu thật từ DB
    // URL truy cập: http://localhost:8085/profile
    @GetMapping("/profile")
    public String showProfilePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {








            return "redirect:/login";
        }

        // Lấy email từ Spring Security Principal
        String email = userDetails.getUsername();
        User currentUser = userService.findByEmail(email);

        // Đẩy đối tượng user thật xuống tầng hiển thị của Thymeleaf
        model.addAttribute("currentUser", currentUser);

        // Tìm trực tiếp file: src/main/resources/templates/profile.html
        return "user/profile";
    }

    // 2. Xử lý lưu thay đổi thông tin cá nhân
    // URL nhận form: http://localhost:8085/profile/update
    @PostMapping("/profile/update")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @RequestParam("fullName") String fullName,
                                @RequestParam("phone") String phone,
                                @RequestParam("address") String address,
                                RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "Có lỗi xảy ra, vui lòng thử lại.");
            return "redirect:/login";
        }

        try {
            String email = userDetails.getUsername();
            User user = userService.findByEmail(email);

            // Cập nhật các trường dữ liệu dựa trên Entity
            user.setFullName(fullName);
            user.setPhone(phone);
            user.setAddress(address);

            userRepository.save(user);
            redirectAttributes.addFlashAttribute("successMsg", "Cập nhật thông tin cá nhân thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Có lỗi xảy ra, vui lòng thử lại.");
        }
        return "redirect:/profile";
    }

    // 3. Xử lý đổi mật khẩu tài khoản
    // URL nhận form: http://localhost:8085/profile/change-password
    @PostMapping("/profile/change-password")
    public String changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        String email = userDetails.getUsername();
        User user = userService.findByEmail(email);

        // Kiểm tra mật khẩu cũ có khớp không
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("passwordError", "Mật khẩu hiện tại không chính xác.");
            return "redirect:/profile?tab=password";
        }

        // Kiểm tra khớp mật khẩu mới lần 2
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("passwordError", "Mật khẩu xác nhận không trùng khớp.");
            return "redirect:/profile?tab=password";
        }

        // Mã hóa mật khẩu mới và lưu lại
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("passwordSuccess", "Đổi mật khẩu thành công!");
        return "redirect:/profile";
    }
}