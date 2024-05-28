package com.william.notix.repositories;

import com.william.notix.entities.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FindingTestReposiltory
    extends JpaRepository<TestResult, Long> {}
