package com.backend.resumemanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.backend.resumemanagement.entity.User;
import com.backend.resumemanagement.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public User register(@RequestBody User user) {

        return authService.register(user);
    }

     @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password
    ) {

        return authService.login(
                email,
                password
        );
    }
}