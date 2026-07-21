package com.backend.resumemanagement.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.backend.resumemanagement.entity.Resume;
import com.backend.resumemanagement.service.ResumeService;

@RestController
@RequestMapping("/api/resumes")
@CrossOrigin("*")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    // Upload Resume
    @PostMapping("/upload")
public Resume uploadResume(
        @RequestParam String name,
        @RequestParam String phone,
        @RequestParam String skills,
        @RequestParam MultipartFile file,
        Authentication authentication) {

    String email = authentication.getName();

    return resumeService.uploadResume(
            name,
            email,
            phone,
            skills,
            file
    );
}

    // Get All Resumes
    @GetMapping
    public List<Resume> getAllResumes() {

        return resumeService.getAllResumes();
    }

    // Search By Name
    @GetMapping("/search")
    public List<Resume> searchResume(
            @RequestParam String name) {

        return resumeService.searchResume(name);
    }

    // Search By Skills
    @GetMapping("/search/skills")
    public List<Resume> searchBySkills(
            @RequestParam String skills) {

        return resumeService.searchBySkills(skills);
    }

    // Get My Resumes
    @GetMapping("/my-resumes")
    public List<Resume> getMyResumes(
            Authentication authentication) {

        String email = authentication.getName();

        return resumeService.getMyResumes(email);
    }

    // Download Resume
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadResume(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();
        Resume resume = resumeService.checkOwner(id, email);
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + resume.getFileName())
                .contentType(MediaType.APPLICATION_PDF)
                .body(
                        resumeService.downloadResume(id));
    }

    // Update Resume
    @PutMapping("/{id}")
    public Resume updateResume(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String skills,
            Authentication authentication) {

        String userEmail = authentication.getName();
        resumeService.checkOwner(id, userEmail);
        return resumeService.updateResume(
                id,
                name,
                email,
                phone,
                skills);
    }

    // Delete Resume
    @DeleteMapping("/{id}")
    public String deleteResume(
            @PathVariable Long id,
            Authentication authentication) {
        String email = authentication.getName();
        resumeService.checkOwner(id, email);
        resumeService.deleteResume(id);
        return "Resume deleted successfully";
    }

    // Get Resume By ID
    @GetMapping("/{id}")
    public Resume getResumeById(
            @PathVariable Long id,
            Authentication authentication) {
        String email = authentication.getName();
        return resumeService.checkOwner(id, email);
    }
}