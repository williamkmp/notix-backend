package com.william.notix.repositories;

import com.william.notix.entities.Finding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FindingRepository extends JpaRepository<Finding, Long> {}
