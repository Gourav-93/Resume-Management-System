package com.backend.resumemanagement.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backend.resumemanagement.entity.Resume;
import com.backend.resumemanagement.repository.ResumeRepository;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;

    public ResumeService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    // Upload Resume
    public Resume uploadResume(
            String name,
            String email,
            String phone,
            MultipartFile file) {

        try {

            Path folder = Paths.get("uploads/resumes");

            Files.createDirectories(folder);

            String fileName = file.getOriginalFilename();

            Path path = folder.resolve(fileName);

            Files.copy(
                    file.getInputStream(),
                    path,
                    StandardCopyOption.REPLACE_EXISTING);

            Resume resume = new Resume();

            resume.setName(name);
            resume.setEmail(email);
            resume.setPhone(phone);
            resume.setFileName(fileName);
            resume.setFilePath(path.toString());
            resume.setUploadedAt(LocalDateTime.now());

            return resumeRepository.save(resume);

        } catch (IOException e) {

            throw new RuntimeException("File upload failed");
        }
    }

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

    public byte[] downloadResume(Long id) {
        Resume resume = getResumeById(id);
        try {
            return Files.readAllBytes(
                    Paths.get(resume.getFilePath()));

        } catch (IOException e) {
            throw new RuntimeException("File download failed");
        }
    }

    public Resume updateResume(
            Long id,
            String name,
            String email,
            String phone) {

        Resume resume = getResumeById(id);

        resume.setName(name);
        resume.setEmail(email);
        resume.setPhone(phone);

        return resumeRepository.save(resume);
    }
}