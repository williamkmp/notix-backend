package com.william.notix.repositories;

import com.william.notix.entities.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLogRespository extends JpaRepository<UserLog, Long> {}
