package com.backend.resumemanagement.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.resumemanagement.entity.Resume;

public interface ResumeRepository
        extends JpaRepository<Resume, Long> {

    List<Resume> findByNameContainingIgnoreCase(
            String name);

    List<Resume> findBySkillsContainingIgnoreCase(
            String skills);

    List<Resume> findByUserEmail(
            String email);

    List<Resume> findTop10ByOrderByUploadedAtDesc();

    Page<Resume> findByNameContainingIgnoreCase(
            String name,
            Pageable pageable);

    Page<Resume> findBySkillsContainingIgnoreCase(
            String skills,
            Pageable pageable);

    long countByUserEmail(String email);

    List<Resume> findTop5ByUserEmailOrderByUploadedAtDesc(
        String email
);
}