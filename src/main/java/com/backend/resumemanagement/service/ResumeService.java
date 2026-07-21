package com.backend.resumemanagement.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backend.resumemanagement.entity.Resume;
import com.backend.resumemanagement.entity.User;
import com.backend.resumemanagement.repository.ResumeRepository;
import com.backend.resumemanagement.repository.UserRepository;

@Service
public class ResumeService {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private UserRepository userRepository;
    
    // Get All Resumes
    public List<Resume> getAllResumes() {
        return resumeRepository.findAll();
    }

    // Get Resume By ID
    public Resume getResumeById(Long id) {
        return resumeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resume not found"));
    }

    // Delete Resume
    public void deleteResume(Long id) {
        Resume resume = getResumeById(id);
        try {
            Path filePath = Paths.get(resume.getFilePath());
            Files.deleteIfExists(filePath);
            resumeRepository.delete(resume);

        } catch (IOException e) {
            throw new RuntimeException("File delete failed");
        }
    }

    // Download Resume
    public byte[] downloadResume(Long id) {
        Resume resume = getResumeById(id);
        try {
            return Files.readAllBytes(
                    Paths.get(resume.getFilePath()));

        } catch (IOException e) {
            throw new RuntimeException("File download failed");
        }
    }

    // Search Resume By Name
    public List<Resume> searchResume(String name) {
        return resumeRepository
                .findByNameContainingIgnoreCase(name);
    }

    // Search Resume By Skills
    public List<Resume> searchBySkills(String skills) {
        return resumeRepository
                .findBySkillsContainingIgnoreCase(skills);
    }

    public List<Resume> getMyResumes(String email) {
        return resumeRepository.findByUserEmail(email);
    }

    public Resume checkOwner(Long id, String email) {
        Resume resume = getResumeById(id);
        if (!resume.getUser().getEmail().equals(email)) {
            throw new RuntimeException(
                    "You are not allowed to access this resume");
        }
        return resume;
    }

    public Resume uploadResume(
        String name,
        String email,
        String phone,
        String skills,
        MultipartFile file) {

    try {

        // Empty file check
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        // PDF check
        if (!file.getContentType().equals("application/pdf")) {
            throw new RuntimeException(
                    "Only PDF files are allowed"
            );
        }

        // 5 MB size limit
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException(
                    "File size must be less than 5 MB"
            );
        }

        // User find
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        // Folder create
        Path folder =
                Paths.get("uploads/resumes");

        Files.createDirectories(folder);

        // File name
        String fileName =
                file.getOriginalFilename();

        // File path
        Path path =
                folder.resolve(fileName);

        // File save
        Files.copy(
                file.getInputStream(),
                path,
                StandardCopyOption.REPLACE_EXISTING
        );

        // Resume object
        Resume resume = new Resume();

        resume.setName(name);
        resume.setEmail(email);
        resume.setPhone(phone);
        resume.setSkills(skills);
        resume.setUser(user);
        resume.setFileName(fileName);
        resume.setFilePath(path.toString());
        resume.setUploadedAt(
                LocalDateTime.now()
        );

        return resumeRepository.save(resume);

    } catch (IOException e) {

        throw new RuntimeException(
                "File upload failed"
        );
    }
}
}