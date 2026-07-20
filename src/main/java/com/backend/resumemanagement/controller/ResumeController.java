package com.backend.resumemanagement.controller;

import java.util.List;

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
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam MultipartFile file
    ) {

        return resumeService.uploadResume(
                name,
                email,
                phone,
                file
        );
    }

    // Get All Resumes
    @GetMapping
    public List<Resume> getAllResumes() {

        return resumeService.getAllResumes();
    }

    // Get Resume By ID
    @GetMapping("/{id}")
    public Resume getResumeById(@PathVariable Long id) {

        return resumeService.getResumeById(id);
    }

    // Delete Resume
    @DeleteMapping("/{id}")
    public String deleteResume(@PathVariable Long id) {

        resumeService.deleteResume(id);

        return "Resume deleted successfully";
    }
}