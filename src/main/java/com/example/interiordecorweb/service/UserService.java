package com.example.interiordecorweb.service;

import com.example.interiordecorweb.dto.RegisterDto;
import com.example.interiordecorweb.entity.Cart;
import com.example.interiordecorweb.entity.Role;
import com.example.interiordecorweb.entity.User;
import com.example.interiordecorweb.repository.CartRepository;
import com.example.interiordecorweb.repository.RoleRepository;
import com.example.interiordecorweb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void register(RegisterDto dto) {

        System.out.println("=== REGISTER START ===");

        Role customerRole = roleRepository.findByRoleName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Role không tồn tại"));

        System.out.println("Found role: " + customerRole.getRoleName());

        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setRole(customerRole);

        userRepository.save(user);

        System.out.println("User saved with id = " + user.getId());

        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);

        System.out.println("Cart created");

        System.out.println("=== REGISTER END ===");
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}