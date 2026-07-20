package com.backend.resumemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.resumemanagement.entity.Resume;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
}
