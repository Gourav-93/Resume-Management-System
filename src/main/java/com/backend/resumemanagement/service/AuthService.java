package com.backend.resumemanagement.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.backend.resumemanagement.entity.User;
import com.backend.resumemanagement.repository.UserRepository;
import com.backend.resumemanagement.security.JwtService;

@Service
public class AuthService {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private JwtService jwtService;

        // Register
        public User register(User user) {

                user.setPassword(
                                passwordEncoder.encode(
                                                user.getPassword()));

                user.setRole("USER");

                return userRepository.save(user);
        }

        // Login
        public String login(
                        String email,
                        String password) {

                User user = userRepository
                                .findByEmail(email)
                                .orElseThrow(() -> new RuntimeException(
                                                "User not found"));

                boolean passwordMatch = passwordEncoder.matches(
                                password,
                                user.getPassword());

                if (!passwordMatch) {

                        throw new RuntimeException(
                                        "Invalid password");
                }

                return jwtService.generateToken(
                                user.getEmail(),
                                user.getRole());
        }
}