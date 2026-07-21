package com.backend.resumemanagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.backend.resumemanagement.entity.User;
import com.backend.resumemanagement.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Get User By Email
    public User getUserByEmail(String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"));
    }

    // Update Profile
    public User updateProfile(
            String email,
            String name) {

        User user =
                getUserByEmail(email);

        user.setName(name);

        return userRepository.save(user);
    }

    // Change Password
    public void changePassword(
            String email,
            String oldPassword,
            String newPassword) {

        User user =
                getUserByEmail(email);

        if (!passwordEncoder.matches(
                oldPassword,
                user.getPassword())) {

            throw new RuntimeException(
                    "Old password is incorrect");
        }

        user.setPassword(
                passwordEncoder.encode(
                        newPassword));

        userRepository.save(user);
    }

    // Forgot Password
    public void forgotPassword(
            String email,
            String newPassword) {

        User user =
                getUserByEmail(email);

        user.setPassword(
                passwordEncoder.encode(
                        newPassword));

        userRepository.save(user);
    }

    // Get All Users
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    // Delete User
    public void deleteUser(Long id) {

        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"));

        userRepository.delete(user);
    }

    // Total Users
    public long getTotalUsers() {

        return userRepository.count();
    }

    // Make Admin
    public User makeAdmin(Long id) {

        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"));

        user.setRole("ADMIN");

        return userRepository.save(user);
    }

    // Remove Admin
    public User removeAdmin(Long id) {

        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"));

        user.setRole("USER");

        return userRepository.save(user);
    }
}