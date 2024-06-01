package com.william.notix.repositories;

import com.william.notix.entities.FindingLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FindingLogReposiory extends JpaRepository<FindingLog, Long> {}
