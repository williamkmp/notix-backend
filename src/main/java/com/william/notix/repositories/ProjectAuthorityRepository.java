package com.william.notix.repositories;

import com.william.notix.entities.ProjectAuthority;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectAuthorityRepository
    extends JpaRepository<ProjectAuthority, Long> {
    /**
     * get authority information from a given user and project
     *
     * @param userId {@link Long} user id
     * @param projectId {@link Long} project id
     * @return {@link Optional}<{@link ProjectAuthority}> user-project authority
     */
    @Query(
        """
            SELECT pa
            FROM project_authorities pa
            WHERE pa.user.id = :userId AND pa.project.id = :projectId
        """
    )
    public Optional<ProjectAuthority> findByUserAndProject(
        @Param("userId") Long userId,
        @Param("projectId") Long projectId
    );
}
