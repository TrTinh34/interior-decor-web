package com.example.interiordecorweb.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDto {
    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @Email(message = "Email không hợp lệ")
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6, message = "Mật khẩu ít nhất 6 ký tự")
    private String password;

    private String phone;
    private String address;
}
