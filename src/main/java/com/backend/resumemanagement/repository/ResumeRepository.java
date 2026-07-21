package com.backend.resumemanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.resumemanagement.entity.Resume;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByNameContainingIgnoreCase(String name);

    List<Resume> findByUserEmail(String email);
    List<Resume> findBySkillsContainingIgnoreCase(String skills);
}