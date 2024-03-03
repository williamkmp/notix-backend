package com.william.notix.repositories;

import com.william.notix.entities.Authority;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    /**
     * get authority information from a given user and project
     *
     * @param userId {@link Long} user id
     * @param projectId {@link Long} project id
     * @return {@link Optional}<{@link Authority}> user-project authority
     */
    @Query(
        """
            SELECT a
            FROM authorities a
            WHERE a.user.id = :userId AND a.project.id = :projectId
        """
    )
    public Optional<Authority> findByUserAndProject(
        @Param("userId") Long userId,
        @Param("projectId") Long projectId
    );
}
