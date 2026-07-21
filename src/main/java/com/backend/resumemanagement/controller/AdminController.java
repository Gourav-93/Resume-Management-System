package com.backend.resumemanagement.controller;

import com.backend.resumemanagement.service.UserService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.backend.resumemanagement.entity.User;

import com.backend.resumemanagement.entity.Resume;
import com.backend.resumemanagement.service.ResumeService;
import com.backend.resumemanagement.service.UserService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ResumeService resumeService;

    // Get All Resumes With Pagination
    @GetMapping("/resumes")
    public Page<Resume> getAllResumes(
            Pageable pageable) {

        return resumeService
                .getAllResumes(pageable);
    }

    // Search By Name
    @GetMapping("/resumes/search")
    public Page<Resume> searchResume(
            @RequestParam String name,
            Pageable pageable) {

        return resumeService.searchResume(
                name,
                pageable);
    }

    // Search By Skills
    @GetMapping("/resumes/search/skills")
    public Page<Resume> searchBySkills(
            @RequestParam String skills,
            Pageable pageable) {

        return resumeService.searchBySkills(
                skills,
                pageable);
    }

    // Download Any Resume
    @GetMapping("/resumes/download/{id}")
    public ResponseEntity<byte[]> downloadResume(
            @PathVariable Long id) {

        Resume resume = resumeService.getResumeById(id);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename="
                                + resume.getFileName())
                .contentType(
                        MediaType.APPLICATION_PDF)
                .body(
                        resumeService
                                .downloadResume(id));
    }

    // Update Any Resume
    @PutMapping("/resumes/{id}")
    public Resume updateResume(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam String skills,
            @RequestParam(required = false) org.springframework.web.multipart.MultipartFile file) {

        return resumeService.updateResume(
                id,
                name,
                phone,
                skills,
                file);
    }

    // Delete Any Resume
    @DeleteMapping("/resumes/{id}")
    public String deleteResume(
            @PathVariable Long id) {

        resumeService.deleteResume(id);

        return "Resume deleted successfully";
    }

    @GetMapping("/stats")
    public Map<String, Long> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put(
                "totalUsers",
                userService.getTotalUsers());

        stats.put(
                "totalResumes",
                resumeService.getTotalResumes());
        return stats;
    }

    @GetMapping("/recent-resumes")
    public List<Resume> getRecentResumes() {

        return resumeService
                .getRecentResumes();
    }

    @GetMapping("/resumes/preview/{id}")
    public ResponseEntity<byte[]> previewResume(
            @PathVariable Long id) {

        Resume resume = resumeService.getResumeById(id);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename="
                                + resume.getFileName())
                .contentType(
                        MediaType.APPLICATION_PDF)
                .body(
                        resumeService.downloadResume(id));
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {

        return userService.getAllUsers();
    }

    @DeleteMapping("/users/{id}")
    public String deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);

        return "User deleted successfully";
    }

    @PutMapping("/users/{id}/make-admin")
    public User makeAdmin(
            @PathVariable Long id) {

        return userService.makeAdmin(id);
    }

    @PutMapping("/users/{id}/remove-admin")
public User removeAdmin(
        @PathVariable Long id) {

    return userService.removeAdmin(id);
}
}