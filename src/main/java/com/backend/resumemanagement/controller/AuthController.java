package com.backend.resumemanagement.controller;

import org.springframework.web.bind.annotation.*;

import com.backend.resumemanagement.entity.User;
import com.backend.resumemanagement.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {

        return authService.register(user);
    }
}