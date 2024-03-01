package com.william.notix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.william.notix.entities.UserLog;

public interface UserLogRespository extends JpaRepository<UserLog, Long> {}
