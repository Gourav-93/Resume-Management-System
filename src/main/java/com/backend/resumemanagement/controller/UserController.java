package com.backend.resumemanagement.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.backend.resumemanagement.entity.Resume;
import com.backend.resumemanagement.entity.User;
import com.backend.resumemanagement.service.ResumeService;
import com.backend.resumemanagement.service.UserService;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ResumeService resumeService;

    // Get My Profile
    @GetMapping("/profile")
    public User getProfile(
            Authentication authentication) {

        String email =
                authentication.getName();

        return userService
                .getUserByEmail(email);
    }

    // Update Profile
    @PutMapping("/profile")
    public User updateProfile(
            @RequestParam String name,
            Authentication authentication) {

        String email =
                authentication.getName();

        return userService
                .updateProfile(
                        email,
                        name);
    }

    // Change Password
    @PutMapping("/change-password")
    public String changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            Authentication authentication) {

        String email =
                authentication.getName();

        userService.changePassword(
                email,
                oldPassword,
                newPassword);

        return "Password changed successfully";
    }

    // Forgot Password
    @PutMapping("/forgot-password")
    public String forgotPassword(
            @RequestParam String email,
            @RequestParam String newPassword) {

        userService.forgotPassword(
                email,
                newPassword);

        return "Password reset successfully";
    }

    // Dashboard
    @GetMapping("/dashboard")
    public Map<String, Long> getDashboard(
            Authentication authentication) {

        String email =
                authentication.getName();

        Map<String, Long> data =
                new HashMap<>();

        data.put(
                "totalResumes",
                resumeService
                        .getMyResumeCount(email));

        return data;
    }

    // Recent Resumes
    @GetMapping("/recent-resumes")
    public List<Resume> getRecentMyResumes(
            Authentication authentication) {

        String email =
                authentication.getName();

        return resumeService
                .getRecentMyResumes(email);
    }
}