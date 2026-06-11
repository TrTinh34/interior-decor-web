package com.example.interiordecorweb.config;

import com.example.interiordecorweb.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    // ✅ Spring Security 7 tự detect Bean này + UserDetailsService bean
    //    => tự tạo DaoAuthenticationProvider, không cần khai báo thủ công
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // 1. Phân quyền cụ thể theo vai trò (Role)
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/cart/**", "/checkout/**").hasAuthority("CUSTOMER")

                        // 2. Bảo vệ trang cá nhân (Chấp nhận cả ADMIN lẫn CUSTOMER miễn là đã đăng nhập)
                        .requestMatchers("/profile", "/profile/**").authenticated()

                        // 3. Các tài nguyên tĩnh và trang công khai không cần đăng nhập
                        .requestMatchers("/", "/products/**", "/register", "/login",
                                "/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()

                        // 4. Các request còn lại phải xác thực
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(customAuthSuccessHandler())
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/access-denied")
                );

        return http.build();
    }

    @Bean


    public AuthenticationSuccessHandler customAuthSuccessHandler() {
        return (request, response, authentication) -> {
            // Kiểm tra xem trong danh sách quyền có ai là ADMIN hoặc ROLE_ADMIN không
            boolean isAdmin = authentication.getAuthorities().stream()
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .anyMatch(role -> "ADMIN".equals(role) || "ROLE_ADMIN".equals(role));

            if (isAdmin) {
                response.sendRedirect("/admin/dashboard");
            } else {
                response.sendRedirect("/");
            }
        };
    }

}