package com.backend.resumemanagement.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.resumemanagement.entity.Resume;
import com.backend.resumemanagement.service.ResumeService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ResumeService resumeService;

    public AdminController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    // Get All Resumes
    @GetMapping("/resumes")
    public List<Resume> getAllResumes() {

        return resumeService.getAllResumes();
    }

    // Search By Name
    @GetMapping("/resumes/search")
    public List<Resume> searchResume(
            @RequestParam String name) {

        return resumeService.searchResume(name);
    }

    // Search By Skills
    @GetMapping("/resumes/search/skills")
    public List<Resume> searchBySkills(
            @RequestParam String skills) {

        return resumeService.searchBySkills(skills);
    }

    // Download Any Resume
    @GetMapping("/resumes/download/{id}")
    public ResponseEntity<byte[]> downloadResume(
            @PathVariable Long id) {

        Resume resume =
                resumeService.getResumeById(id);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename="
                                + resume.getFileName()
                )
                .contentType(
                        MediaType.APPLICATION_PDF
                )
                .body(
                        resumeService.downloadResume(id)
                );
    }

    // Delete Any Resume
    @DeleteMapping("/resumes/{id}")
    public String deleteResume(
            @PathVariable Long id) {

        resumeService.deleteResume(id);

        return "Resume deleted successfully";
    }
}