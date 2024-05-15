package com.william.notix.repositories;

import com.william.notix.entities.ProjectFileDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectFileRepository
    extends JpaRepository<ProjectFileDetail, Long> {}
