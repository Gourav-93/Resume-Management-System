package com.backend.resumemanagement.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.backend.resumemanagement.entity.Resume;
import com.backend.resumemanagement.service.ResumeService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

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
            @RequestParam String skills,
            @RequestParam MultipartFile file) {

        return resumeService.uploadResume(
                name,
                email,
                phone,
                skills,
                file);
    }

    // Get All Resumes
    @GetMapping
    public List<Resume> getAllResumes() {

        return resumeService.getAllResumes();
    }

    // Delete Resume
    @DeleteMapping("/{id}")
    public String deleteResume(@PathVariable Long id) {

        resumeService.deleteResume(id);

        return "Resume deleted successfully";
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadResume(@PathVariable Long id) {

        Resume resume = resumeService.getResumeById(id);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + resume.getFileName())
                .contentType(MediaType.APPLICATION_PDF)
                .body(resumeService.downloadResume(id));
    }

    @PutMapping("/{id}")
    public Resume updateResume(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String skills) {

        return resumeService.updateResume(
                id,
                name,
                email,
                phone,
                skills);
    }

    @GetMapping("/search")
    public List<Resume> searchResume(
            @RequestParam String name) {

        return resumeService.searchResume(name);
    }

    @GetMapping("/search/skills")
    public List<Resume> searchBySkills(
            @RequestParam String skills) {

        return resumeService.searchBySkills(skills);
    }

    @GetMapping("/{id}")
    public Resume getResumeById(@PathVariable Long id) {

        return resumeService.getResumeById(id);
    }
}