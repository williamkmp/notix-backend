package com.william.notix.repositories;

import com.william.notix.entities.Discussion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscussionRepository extends JpaRepository<Discussion, Long> {}
