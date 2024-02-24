package com.william.notix.repositories;

import com.william.notix.entities.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * find user by email
     *
     * @param email {@link String} email
     * @return
     */
    Optional<User> findByEmail(String email);
}
