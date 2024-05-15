package com.william.notix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.william.notix.entities.ProjectFileDetail;

public interface ProjectFileRepository extends JpaRepository<ProjectFileDetail, Long> {}
