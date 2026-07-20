package com.backend.resumemanagement.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.backend.resumemanagement.entity.User;
import com.backend.resumemanagement.repository.UserRepository;
import com.backend.resumemanagement.security.JwtService;

@Service
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User register(User user) {

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        return userRepository.save(user);
    }

    public String login(String email, String password) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        boolean passwordMatch =
                passwordEncoder.matches(
                        password,
                        user.getPassword()
                );

        if (!passwordMatch) {

            throw new RuntimeException(
                    "Invalid password"
            );
        }

        return jwtService.generateToken(
                user.getEmail()
        );
    }
}