package com.william.notix.repositories;

import com.william.notix.entities.FindingMemberRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FindingMemberRepository
    extends JpaRepository<FindingMemberRecord, Long> {}
