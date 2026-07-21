package com.backend.resumemanagement.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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

    // Get All Resumes With Pagination
    public Page<Resume> getAllResumes(
            Pageable pageable) {

        return resumeRepository.findAll(pageable);
    }

    // Get Resume By ID
    public Resume getResumeById(Long id) {

        return resumeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Resume not found"));
    }

    // Delete Resume
    public void deleteResume(Long id) {

        Resume resume = getResumeById(id);

        try {

            Path filePath = Paths.get(resume.getFilePath());

            Files.deleteIfExists(filePath);

            resumeRepository.delete(resume);

        } catch (IOException e) {

            throw new RuntimeException(
                    "File delete failed");
        }
    }

    // Download Resume
    public byte[] downloadResume(Long id) {

        Resume resume = getResumeById(id);

        try {

            return Files.readAllBytes(
                    Paths.get(
                            resume.getFilePath()));

        } catch (IOException e) {

            throw new RuntimeException(
                    "File download failed");
        }
    }

    // Search Resume By Name
    public List<Resume> searchResume(
            String name) {

        return resumeRepository
                .findByNameContainingIgnoreCase(
                        name);
    }

    // Search Resume By Skills
    public List<Resume> searchBySkills(
            String skills) {

        return resumeRepository
                .findBySkillsContainingIgnoreCase(
                        skills);
    }

    // Get My Resumes
    public List<Resume> getMyResumes(
            String email) {

        return resumeRepository
                .findByUserEmail(email);
    }

    // Check Resume Owner
    public Resume checkOwner(
            Long id,
            String email) {

        Resume resume = getResumeById(id);

        if (!resume.getUser()
                .getEmail()
                .equals(email)) {

            throw new RuntimeException(
                    "You are not allowed to access this resume");
        }

        return resume;
    }

    // Upload Resume
    public Resume uploadResume(
            String name,
            String email,
            String phone,
            String skills,
            MultipartFile file) {

        try {

            // Empty File Check
            if (file.isEmpty()) {

                throw new RuntimeException(
                        "File is empty");
            }

            // PDF Check
            if (!"application/pdf"
                    .equals(file.getContentType())) {

                throw new RuntimeException(
                        "Only PDF files are allowed");
            }

            // 5 MB Size Limit
            if (file.getSize() > 5 * 1024 * 1024) {

                throw new RuntimeException(
                        "File size must be less than 5 MB");
            }

            // Find User
            User user = userRepository
                    .findByEmail(email)
                    .orElseThrow(() -> new RuntimeException(
                            "User not found"));

            // Create Folder
            Path folder = Paths.get(
                    "uploads/resumes");

            Files.createDirectories(folder);

            // Original File Name
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

            // Unique File Name
            String fileName = System.currentTimeMillis()
                    + "_"
                    + originalFileName;

            // File Path
            Path path = folder.resolve(fileName);

            // Save File
            Files.copy(
                    file.getInputStream(),
                    path,
                    StandardCopyOption.REPLACE_EXISTING);

            // Create Resume
            Resume resume = new Resume();

            resume.setName(name);
            resume.setEmail(email);
            resume.setPhone(phone);
            resume.setSkills(skills);
            resume.setUser(user);
            resume.setFileName(fileName);
            resume.setFilePath(
                    path.toString());
            resume.setUploadedAt(
                    LocalDateTime.now());

            return resumeRepository.save(
                    resume);

        } catch (IOException e) {

            throw new RuntimeException(
                    "File upload failed");
        }
    }

    // Update Resume
    public Resume updateResume(
            Long id,
            String name,
            String phone,
            String skills,
            MultipartFile file) {

        Resume resume = getResumeById(id);

        // Update Details
        resume.setName(name);
        resume.setPhone(phone);
        resume.setSkills(skills);

        // If New File Uploaded
        if (file != null &&
                !file.isEmpty()) {

            // PDF Check
            if (!"application/pdf"
                    .equals(file.getContentType())) {

                throw new RuntimeException(
                        "Only PDF files are allowed");
            }

            // 5 MB Limit
            if (file.getSize() > 5 * 1024 * 1024) {

                throw new RuntimeException(
                        "File size must be less than 5 MB");
            }

            try {

                // Delete Old File
                Files.deleteIfExists(
                        Paths.get(
                                resume.getFilePath()));

                // Create Folder
                Path folder = Paths.get(
                        "uploads/resumes");

                Files.createDirectories(
                        folder);

                // Original File Name
                String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

                // Unique File Name
                String fileName = System.currentTimeMillis()
                        + "_"
                        + originalFileName;

                // New File Path
                Path path = folder.resolve(fileName);

                // Save New File
                Files.copy(
                        file.getInputStream(),
                        path,
                        StandardCopyOption.REPLACE_EXISTING);

                // Update File Details
                resume.setFileName(
                        fileName);

                resume.setFilePath(
                        path.toString());

            } catch (IOException e) {

                throw new RuntimeException(
                        "File update failed");
            }
        }

        return resumeRepository.save(
                resume);
    }

    // Get Total Resumes
    public long getTotalResumes() {

        return resumeRepository.count();
    }

    // Get Recent Resumes
    public List<Resume> getRecentResumes() {

        return resumeRepository
                .findTop10ByOrderByUploadedAtDesc();
    }

    public Page<Resume> searchResume(
            String name,
            Pageable pageable) {

        return resumeRepository
                .findByNameContainingIgnoreCase(
                        name,
                        pageable);
    }

    public Page<Resume> searchBySkills(
            String skills,
            Pageable pageable) {

        return resumeRepository
                .findBySkillsContainingIgnoreCase(
                        skills,
                        pageable);
    }

    public long getMyResumeCount(String email) {

    return resumeRepository
            .countByUserEmail(email);
}

public List<Resume> getRecentMyResumes(
        String email) {

    return resumeRepository
            .findTop5ByUserEmailOrderByUploadedAtDesc(
                    email);
}

}